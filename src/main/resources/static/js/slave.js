var iceServer = {
  "iceServers": [{
    "url": "stun:stun.l.google.com:19302"
  }, {
    "url": "turn:numb.viagenie.ca",
    "username": "webrtc@live.com",
    "credential": "muazkh"
  }]
};

var socket;

let host = 'ws://192.168.29.54';

let remoteStream;
let remotePeerConnection;


// Define peer connections, streams and video elements.
const remoteVideo = document.getElementById('remoteVideo');

remoteVideo.addEventListener('loadedmetadata', logVideoLoaded);
remoteVideo.addEventListener('onresize', logResizedVideo);
// Logs a message with the id and size of a video element.
function logVideoLoaded(event) {
  const video = event.target;
  console.log(`${video.id} videoWidth: ${video.videoWidth}px, ` +
    `videoHeight: ${video.videoHeight}px.`);
}

// Logs a message with the id and size of a video element.
// This event is fired when video begins streaming.
function logResizedVideo(event) {
  logVideoLoaded(event);

  if (startTime) {
    const elapsedTime = window.performance.now() - startTime;
    startTime = null;
    console.log(`Setup time: ${elapsedTime.toFixed(3)}ms.`);
  }
}

// Define action buttons.
const callButton = document.getElementById('callButton');
const hangupButton = document.getElementById('hangupButton');
// Set up initial action buttons status: disable call and hangup.
callButton.disabled = false;
hangupButton.disabled = true;

// Add click event handlers for buttons.
callButton.addEventListener('click', callAction);
hangupButton.addEventListener('click', hangupAction);

// Handles call button action: creates peer connection.
function callAction() {
  callButton.disabled = true;
  hangupButton.disabled = false;
  console.log('Starting call.');

  startTime = window.performance.now();

  initSocket();

  remotePeerConnection = new RTCPeerConnection(iceServer);
  console.log('Created remote peer connection object remotePeerConnection.');

  remotePeerConnection.addEventListener('icecandidate', handleConnection);
  remotePeerConnection.addEventListener(
    'iceconnectionstatechange', handleConnectionChange);
  remotePeerConnection.addEventListener('addstream', gotRemoteMediaStream);

  // Add local stream to connection and create offer to connect.

}
function initSocket(){
  socket = new WebSocket(host+":8080/meeting-online");
  socket.onmessage = onSocketMessage;
}

function handleConnection(event) {
  if (event.candidate !== null) {
    socket.send(JSON.stringify({
      "event": "_ice_candidate",
      "data": {
        "candidate": event.candidate
      }
    }));
  }

  console.log(`remotePeerConnection ICE candidate:\n` +
    `${event.candidate}.`);
}
// Logs changes to the connection state.
function handleConnectionChange(event) {
  console.log('ICE state change event: ', event);
  console.log(`remotePeerConnection ICE state: ` +
    `${event.target.iceConnectionState}.`);
}
// Handles remote MediaStream success by adding it as the remoteVideo src.
function gotRemoteMediaStream(event) {
  const mediaStream = event.stream;
  remoteVideo.srcObject = mediaStream;
  remoteStream = mediaStream;
  console.log('Remote peer connection received remote stream.');
}


// Handles hangup action: ends up call, closes connections and resets peers.
function hangupAction() {
  remotePeerConnection.close();
  socket.close();
  remotePeerConnection = null;
  remoteVideo.srcObject = null;
  remoteStream = null;
  hangupButton.disabled = true;
  callButton.disabled = false;
  console.log('Ending call.');
}

onSocketMessage = function(event){
  var json = JSON.parse(event.data);
  console.log('onmessage: ', json);
  //如果是一个ICE的候选，则将其加入到PeerConnection中，否则设定对方的session描述为传递过来的描述
  if( json.event === "_ice_candidate" ){
    console.log("add ice candidate ", json.data.candidate)
    remotePeerConnection.addIceCandidate(new RTCIceCandidate(json.data.candidate));
  } else {
    remotePeerConnection.setRemoteDescription(new RTCSessionDescription(json.data.sdp));
    // 如果是一个offer，那么需要回复一个answer
    if(json.event === "_offer") {
      remotePeerConnection.createAnswer(sendAnswerFn, function (error) {
        console.log('Failure callback: ' + error);
      });
    }
  }
};

sendAnswerFn = function(desc){
  remotePeerConnection.setLocalDescription(desc);
  socket.send(JSON.stringify({
    "event": "_answer",
    "data": {
      "sdp": desc
    }
  }));
};

