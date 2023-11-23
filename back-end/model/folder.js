const mongoose = require("mongoose");
const Schema = mongoose.Schema
const ObjectId = Schema.Types.ObjectId

const folderSchema = new mongoose.Schema({
    _id: { type: String, default: null},
    userId: { type: String, default: null},
    name: { type: String, default: null },
    description: { type: String, default: null }
},);
folderSchema.set("timestamps", true);
module.exports = mongoose.model("folder", folderSchema);