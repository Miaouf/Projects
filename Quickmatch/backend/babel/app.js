import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import "babel-polyfill";
import Player from "./src/controllers/player";
import Club from "./src/controllers/club";

dotenv.config();
console.log(process.env.DATABASE_URL);
const app = express();

app.use(express.json());
app.use(cors());

app.get("/", (req, res) => {
  return res
    .status(200)
    .send({ message: "YAY! Congratulations! Your first endpoint is working" });
});

app.post("/api/v1/Players", Player.create);
app.get("/api/v1/Players", Player.getAll);
app.get("/api/v1/Playersrows", Player.getAllrows);
app.get("/api/v1/Players/:id", Player.getOne);
app.put("/api/v1/Players/:id", Player.update);
app.delete("/api/v1/Players/:id", Player.delete);

app.post("/api/v1/Clubs", Club.create);
app.get("/api/v1/Clubs", Club.getAll);
app.get("/api/v1/Clubs/:id", Club.getOne);
app.put("/api/v1/Clubs/:id", Club.update);
app.delete("/api/v1/Clubs/:id", Club.delete);

app.listen(3000);
console.log("app running on port ", 3000);
