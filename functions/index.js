const functions = require("firebase-functions");
const admin = require("firebase-admin");
const express = require("express");
const cors = require("cors");

admin.initializeApp();
const db = admin.firestore();
const app = express();

app.use(cors({ origin: true }));
app.use(express.json());

function toTaskDto(doc) {
  const data = doc.data() || {};
  return {
    id: doc.id,
    title: data.title ?? "",
    tag: data.tag ?? "",
    priority: data.priority ?? "NORMAL",
    isCompleted: data.isCompleted ?? false,
  };
}

function tasksRef(userId) {
  return db.collection("users").doc(userId).collection("tasks");
}

app.post("/auth/signup", async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ error: "Missing fields" });
    const user = await admin.auth().createUser({ email, password });
    const token = await admin.auth().createCustomToken(user.uid);
    res.status(201).json({ id: user.uid, email: user.email, token });
  } catch (e) {
    console.error(e);
    if (e.code === "auth/email-already-exists") {
      return res.status(409).json({ error: "Account already exists" });
    }
    res.status(500).json({ error: e.message });
  }
});

app.post("/auth/login", async (req, res) => {
  try {
    const { email } = req.body;
    if (!email) return res.status(400).json({ error: "Missing fields" });
    const user = await admin.auth().getUserByEmail(email);
    const token = await admin.auth().createCustomToken(user.uid);
    res.json({ id: user.uid, email: user.email, token });
  } catch (e) {
    console.error(e);
    if (e.code === "auth/user-not-found") {
      return res.status(401).json({ error: "Invalid email or password" });
    }
    res.status(500).json({ error: e.message });
  }
});

app.get("/tasks", async (req, res) => {
  try {
    const { userId } = req.query;
    if (!userId) return res.status(400).json({ error: "Missing userId" });
    const snap = await tasksRef(userId).get();
    res.json(snap.docs.map(toTaskDto));
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

app.get("/tasks/:id", async (req, res) => {
  try {
    const { userId } = req.query;
    if (!userId) return res.status(400).json({ error: "Missing userId" });
    const doc = await tasksRef(userId).doc(req.params.id).get();
    if (!doc.exists) return res.status(404).json({ error: "Not found" });
    res.json(toTaskDto(doc));
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

app.post("/tasks", async (req, res) => {
  try {
    const { userId, title, tag, priority, isCompleted } = req.body;
    if (!userId || !title) return res.status(400).json({ error: "Missing fields" });
    const ref = await tasksRef(userId).add({
      title,
      tag: tag ?? "",
      priority: priority ?? "NORMAL",
      isCompleted: isCompleted ?? false,
    });
    const doc = await ref.get();
    res.status(201).json(toTaskDto(doc));
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

app.put("/tasks/:id", async (req, res) => {
  try {
    const { userId, title, tag, priority, isCompleted } = req.body;
    if (!userId) return res.status(400).json({ error: "Missing userId" });
    const ref = tasksRef(userId).doc(req.params.id);
    const doc = await ref.get();
    if (!doc.exists) return res.status(404).json({ error: "Not found" });
    await ref.set({
      title: title ?? "",
      tag: tag ?? "",
      priority: priority ?? "NORMAL",
      isCompleted: isCompleted ?? false,
    });
    const updated = await ref.get();
    res.json(toTaskDto(updated));
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

app.patch("/tasks/:id", async (req, res) => {
  try {
    const { userId, isCompleted } = req.body;
    if (!userId || isCompleted === undefined) return res.status(400).json({ error: "Missing fields" });
    const ref = tasksRef(userId).doc(req.params.id);
    const doc = await ref.get();
    if (!doc.exists) return res.status(404).json({ error: "Not found" });
    await ref.update({ isCompleted });
    const updated = await ref.get();
    res.json(toTaskDto(updated));
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

app.delete("/tasks/:id", async (req, res) => {
  try {
    const { userId } = req.query;
    if (!userId) return res.status(400).json({ error: "Missing userId" });
    await tasksRef(userId).doc(req.params.id).delete();
    res.status(204).send();
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

exports.api = functions.https.onRequest(app);
