const mongoose = require("mongoose");
const fileSchema = new mongoose.Schema({
    _id: { type: String, default: null},
    folderId: { type: String, default: null},
    name: { type: String, default: null }
},);
fileSchema.set("timestamps", true);
module.exports = mongoose.model("file", fileSchema);