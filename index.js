const express = require('express')
const app = express()
const PORT = 8080

var nrOfParticipant = 0
var nrOfChatMessages = 0
var chat = {participantId: [], message: []}
var backendInUse = false

class EndpointFunctions {
    static updateNrOfParticipant(req, res) {
        res.send(nrOfParticipant.toString())
        nrOfParticipant++
    }

    static sendMessage(req, res) {
        chat.participantId.push(req.query.id)
        chat.message.push(req.query.message)
        nrOfChatMessages++
    }

    static getMessageJson(req, res) {
        var messageJson = {participantId: [], message: []}
        for (var i = parseInt(req.query.lastMessageId); i < nrOfChatMessages; i++) {
            messageJson.participantId.push(chat.participantId[i])
            messageJson.message.push(chat.message[i])
        }
        res.send(JSON.stringify(messageJson))
    }

    static clearStorage(req, res) {
        nrOfParticipant = 0
        nrOfChatMessages = 0
        chat = {participantId: [], message: []}
    }

    static getNrOfChatMessages(req, res) {
        res.send(nrOfChatMessages.toString())
    }
}

function endpointFunctionHandler(req, res, foo) {
    if (!backendInUse) {
        backendInUse = true
        try {
            foo(req, res)
        } finally {
            backendInUse = false
        }
    } else {
        res.send('serviceUnavailable')
    }
}

app.get('/participant', (req, res) => {
    endpointFunctionHandler(req, res, EndpointFunctions.updateNrOfParticipant)
})

app.get('/sendMessage', (req, res) => {
    endpointFunctionHandler(req, res, EndpointFunctions.sendMessage)
})

app.get('/getMessage', (req, res) => {
    endpointFunctionHandler(req, res, EndpointFunctions.getMessageJson)
})

app.get('/clearStorage', (req, res) => {
    endpointFunctionHandler(req, res, EndpointFunctions.clearStorage)
})

app.get('/getNrOfChatMessages', (req, res) => {
    endpointFunctionHandler(req, res, EndpointFunctions.getNrOfChatMessages)
})

app.listen(PORT, () => console.log('Server is running'))
