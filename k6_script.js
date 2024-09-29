import {check} from 'k6';
import http from 'k6/http';

export let options = {
    stages: [
        {duration: '30s', target: 1}, // warm up
        {duration: '1m', target: 10}, // warm up
        {duration: '1m', target: 100}, // ramp up to N users over N minutes
        {duration: '1m', target: 1000}, // stay at N users for N minutes
        // {duration: '2m', target: 0}, // ramp down to 0 users over 1 minute
    ],
};

const BASE_URL = 'http://localhost:8080/games'; // docker-compose = 9999 or else 8080

export default function () {
    let playerName = `Player${Math.floor(Math.random() * 10000)}`;
    let gameId = uuidv4() + "-" + playerName;

    // Start a game
    let startGameRes = http.put(`${BASE_URL}/${gameId}?playerName=${playerName}`);
    check(startGameRes, {'Game started successfully': (resp) => resp.status === 201});
    // let json = JSON.parse(startGameRes.body);
    // console.log(startGameRes)

    // sleep(5);

    const moves = ['ROCK', 'PAPER', 'SCISSORS'];
    let playerMove = moves[Math.floor(Math.random() * moves.length)];  // Randomizes move between 'ROCK', 'PAPER', 'SCISSORS'

    // Play a round
    let playRoundRes = http.put(`${BASE_URL}/${gameId}/plays?playerMove=${playerMove}`);
    check(playRoundRes, {'Move made successfully': (resp) => resp.status === 201});

    // sleep(1);

    // End the game
    let endGameRes = http.put(`${BASE_URL}/${gameId}/endings`);
    check(endGameRes, {'Game ended successfully': (resp) => resp.status === 201});

    // // sleep(1);
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}