let peer = null;
let connections = [];
let myId = null;

// Initialization function
function init(userIdBytes) {
    var userId = byteArrayToString(userIdBytes);

    // Create a Peer instance
    peer = new Peer(userId, {
        port: 443,
        path: '/',
    });

    // Event handlers for the Peer instance
    peer.on('open', handlePeerOpen);
    peer.on('connection', handleConnection);
    peer.on('close', handlePeerClose);
    peer.on('disconnected', handlePeerDisconnected);
}

// Convert byte array to string
function byteArrayToString(byteArray) {
    const decoder = new TextDecoder('utf-8');
    return decoder.decode(new Uint8Array(byteArray));
}

// Handle peer open event
function handlePeerOpen() {
    myId = peer.id;
    Android.onConnected();
}

// Handle peer close event
function handlePeerClose() {
    Android.onClose(peer.id);
}

// Handle peer disconnected event
function handlePeerDisconnected() {
    Android.send("user disconnected");
}

// Handle incoming connections
function handleConnection(connection) {
    connections.push(connection);
    connection.on('data', handleData);
}

// Connect to another peer
function connect(otherIdBytes) {
    var otherId = byteArrayToString(otherIdBytes);
    let conn = peer.connect(otherId, { reliable: true });

    conn.on('open', () => {
        Android.onConnected(conn.peer);
        handleConnection(conn);
    });
}

// Send file data to all connections
function sendFileData(fileId,fileName,index, bytes,curSize, size) {
    var data = {
        type: 'fileData',
        id: myId,
        fileId: fileId,
        fileName: fileName,
        index: index,
        bytes: bytes,
        curSize: curSize,
        size: size
    };

    sendToAllConnections(data);
}

// Send message to all connections
function sendMessage(name, message, millis) {
    var data = {
        type: 'message',
        id: myId,
        name: name,
        message: message,
        millis: millis
    };

    sendToAllConnections(data);
}

// Handle incoming data
function handleData(data) {
    if (data.type === 'message') {
        Android.showMessage(data.id, data.name, data.message, data.millis);
    } else if (data.type === 'fileData') {
        Android.receiveFileData(data.fileId,data.fileName,data.id, data.index, data.bytes,data.curSize, data.size);
    }
}

// Send data to all open connections
function sendToAllConnections(data) {
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}
