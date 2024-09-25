import {check} from 'k6';
import http from 'k6/http';

export let options = {
    stages: [
        {duration: '30s', target: 1}, // warm up
        {duration: '1m', target: 10}, // warm up
        // {duration: '1m', target: 100}, // ramp up to N users over N minutes
        // {duration: '1m', target: 1000}, // stay at N users for N minutes
        // {duration: '2m', target: 0}, // ramp down to 0 users over 1 minute
    ],
};

const BASE_URL = 'http://localhost:8080/games'; // Replace with your application URL

export default function () {
    let playerName = `Player${Math.floor(Math.random() * 10000)}`;

    // Start a game
    let startGameRes = http.post(`${BASE_URL}?playerName=${playerName}`);
    check(startGameRes, {'Game started successfully': (resp) => resp.status === 201});
    let json = JSON.parse(startGameRes.body);
    // console.log(startGameRes)
    let gameId = json.gameId;

    // sleep(1);

    const moves = ['ROCK', 'PAPER', 'SCISSORS'];
    let playerMove = moves[Math.floor(Math.random() * moves.length)];  // Randomizes move between 'ROCK', 'PAPER', 'SCISSORS'

    // Play a round
    let playRoundRes = http.put(`${BASE_URL}/${gameId}/plays?playerMove=${playerMove}`);
    check(playRoundRes, {'Move made successfully': (resp) => resp.status === 201});

    // sleep(1);

    // End the game
    let endGameRes = http.put(`${BASE_URL}/${gameId}/endings`);
    check(endGameRes, {'Game ended successfully': (resp) => resp.status === 201});

    // sleep(1);
}