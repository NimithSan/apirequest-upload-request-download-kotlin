const mongoose = require("mongoose");

const userSchema = new mongoose.Schema({
  _id: { type: String, default: null},
  first_name: { type: String, default: null },
  last_name: { type: String, default: null },
  email: { type: String, unique: true },
  password: { type: String },
  token: { type: String },
});
userSchema.set("timestamps", true);
module.exports = mongoose.model("user", userSchema);