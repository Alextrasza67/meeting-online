const mediaStreamConstraints = {
  video: true,
  audio: true
};
const offerOptions = {
  offerToReceiveVideo: 1,
};
const localVideo = document.getElementById('localVideo');

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
}


let localStream;
let remoteStream;
let localPeerConnection;

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
var stompClient;
let host = 'ws://192.168.33.172';
// let host = 'ws://192.168.3.6';

const loginButton = document.getElementById('loginButton');
const prepareButton = document.getElementById('prepareButton');
const startButton = document.getElementById('startButton');
const hangupButton = document.getElementById('hangupButton');

prepareButton.disabled = true;
startButton.disabled = true;
hangupButton.disabled = true;

// Add click event handlers for buttons.
loginButton.addEventListener('click', loginAction);
prepareButton.addEventListener('click', prepareAction);
startButton.addEventListener('click', startAction);
hangupButton.addEventListener('click', hangupAction);


function prepareAction(){
  prepareButton.disabled = true;
  startButton.disabled = false;
  hangupButton.disabled = true;
  initPeer();
}

function loginAction(){
  initSocket();
}

function initSocket(){
  socket = new SockJS("/meeting/");
  stompClient = Stomp.over(socket);
  var headers = {
    roomId: $("#roomId").val(),
    username: $("#username").val(),
    password: $("#password").val()
  };
  stompClient.connect(headers, function (frame) {
    prepareButton.disabled = false;
    console.log('Connected: ' + frame);
    stompClient.subscribe('/user/' + $("#username").val() + '/meetingRoom/'+$("#roomId").val(), function (msg) {
      var json = JSON.parse(msg.body);
      console.log('onmessage: ', json);
      //如果是一个ICE的候选，则将其加入到PeerConnection中，否则设定对方的session描述为传递过来的描述
      if( json.event === "_ice_candidate" ){
        console.log("add ice candidate ", json.data.candidate)
        localPeerConnection.addIceCandidate(new RTCIceCandidate(json.data.candidate));
      } else {
        localPeerConnection.setRemoteDescription(new RTCSessionDescription(json.data.sdp));
        // 如果是一个offer，那么需要回复一个answer
        if(json.event === "_offer") {
          localPeerConnection.createAnswer(sendAnswerFn, function (error) {
            console.log('Failure callback: ' + error);
          });
        }
      }
    });
  });
}






function initPeer(){
  // Create peer connections and add behavior.
  localPeerConnection = new RTCPeerConnection(iceServer);
  console.log('Created local peer connection object localPeerConnection.');

  localPeerConnection.addEventListener('icecandidate', handleConnection);
  localPeerConnection.addEventListener(
    'iceconnectionstatechange', handleConnectionChange);
  localPeerConnection.addEventListener('addstream', gotRemoteMediaStream);
}

// Handles remote MediaStream success by adding it as the remoteVideo src.
function gotRemoteMediaStream(event) {
  const mediaStream = event.stream;
  remoteVideo.srcObject = mediaStream;
  remoteStream = mediaStream;
  console.log('Remote peer connection received remote stream.');
}









function startAction() {
  prepareButton.disabled = true;
  startButton.disabled = true;
  hangupButton.disabled = false;
  navigator.mediaDevices.getUserMedia(mediaStreamConstraints)
    .then(gotLocalMediaStream).catch(handleLocalMediaStreamError);
  console.log('Requesting local stream.');
}

// Handles error by logging a message to the console.
function handleLocalMediaStreamError(error) {
  console.log(`navigator.getUserMedia error: ${error.toString()}.`);
}

function gotLocalMediaStream(mediaStream) {
  localVideo.srcObject = mediaStream;
  localStream = mediaStream;
  console.log('Received local stream.');
  callAction();
}

// Handles call button action: creates peer connection.
function callAction() {
  console.log('Starting call.');
  startTime = window.performance.now();

  // Get local media stream tracks.
  const videoTracks = localStream.getVideoTracks();
  const audioTracks = localStream.getAudioTracks();
  if (videoTracks.length > 0) {
    console.log(`Using video device: ${videoTracks[0].label}.`);
  }
  if (audioTracks.length > 0) {
    console.log(`Using audio device: ${audioTracks[0].label}.`);
  }

  // Add local stream to connection and create offer to connect.
  localPeerConnection.addStream(localStream);
  console.log('Added local stream to localPeerConnection.');

  console.log('localPeerConnection createOffer start.');
  localPeerConnection.createOffer(offerOptions)
    .then(createdOffer).catch(setSessionDescriptionError);
}

function handleConnection(event) {
  if (event.candidate !== null) {
    stompClient.send("/app/meetingRoomServer/"+$("#roomId").val(), {}, JSON.stringify({
      "event": "_ice_candidate",
      "data": {
        "candidate": event.candidate
      }
    }));
  }

  console.log(`localPeerConnection ICE candidate:\n` +
    `${event.candidate}.`);
}

// Logs changes to the connection state.
function handleConnectionChange(event) {
  console.log('ICE state change event: ', event);
  console.log(`localPeerConnection ICE state: ` +
    `${event.target.iceConnectionState}.`);
}

// Logs error when setting session description fails.
function setSessionDescriptionError(error) {
  console.log(`Failed to create session description: ${error.toString()}.`);
}

function createdOffer(description) {
  console.log(`Offer from localPeerConnection:\n${description.sdp}`);
  console.log('localPeerConnection setLocalDescription start.');
  localPeerConnection.setLocalDescription(description)
    .then(() => {
      console.log(`localPeerConnection setLocalDescription complete.`);
    }).catch(setSessionDescriptionError);

  stompClient.send("/app/meetingRoomServer/"+$("#roomId").val(), {}, JSON.stringify({
    "event": "_offer",
    "data": {
      "sdp": description
    }
  }));
}

function hangupAction() {
  localPeerConnection.close();
  stompClient.disconnect();
  socket.close();
  localStream.getTracks().forEach(function(track){
    track.stop();
  })
  localPeerConnection = null;

  localStream = null;
  remoteStream = null;
  localVideo.srcObject = null;
  remoteVideo.srcObject = null;

  prepareButton.disabled = false;
  startButton.disabled = true;
  hangupButton.disabled = true;

  console.log('Ending call.');
}

onSocketMessage = function(event){
  var json = JSON.parse(event.data);
  console.log('onmessage: ', json);
  //如果是一个ICE的候选，则将其加入到PeerConnection中，否则设定对方的session描述为传递过来的描述
  if( json.event === "_ice_candidate" ){
    console.log("add ice candidate ", json.data.candidate)
    localPeerConnection.addIceCandidate(new RTCIceCandidate(json.data.candidate));
  } else {
    localPeerConnection.setRemoteDescription(new RTCSessionDescription(json.data.sdp));
    // 如果是一个offer，那么需要回复一个answer
    if(json.event === "_offer") {
      localPeerConnection.createAnswer(sendAnswerFn, function (error) {
        console.log('Failure callback: ' + error);
      });
    }
  }
};

sendAnswerFn = function(desc){
  localPeerConnection.setLocalDescription(desc);
  stompClient.send("/app/meetingRoomServer/"+$("#roomId").val(), {}, JSON.stringify({
    "event": "_answer",
    "data": {
      "sdp": desc
    }
  }));
};