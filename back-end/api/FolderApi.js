const Folder = require("../model/folder");
module.exports = {
    createFolder: async(req, res)=>{
        try {
            const { _id , name, description } = req.body;
            console.log("PostData", _id, name, description, req.user)
            if (!(_id && name)) {
                return res.status(400).send("Id, name are required");
            }
            
            const oldFolder = await Folder.findOne({_id:_id});
            if (oldFolder) {
                return res.status(409).send({error: "Folder Already Exist.",folder: oldFolder});
            }
            const folder = await Folder.create({
                _id: _id,
                userId: req.user.user_id,
                name: name,
                description: description
            });
            res.status(201).json(folder);
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    updateFolder: async(req, res)=>{
        try {
            const { _id , name, description } = req.body;
            console.log("Update Data", _id, name, description, req.user)
            if (!(_id && name)) {
                return res.status(400).send({ error: "Id, name are required"});
            }
            
            const folder = await Folder.findOne({_id:_id, userId: req.user.user_id});
            if(!folder){
                return res.status(400).send({error: "Folder doesn't exist.",folder: folder});
            }
            folder.name = name;
            folder.description = description;
            let newValue = {$set: {name: name, description: description}}
            let result = await Folder.updateOne({_id: _id}, newValue);
            res.status(201).json(folder);
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    deleteFolder: async(req, res)=>{
        try {
            const { _id } = req.body;
            if (!_id) {
                return res.status(400).send({error: "Id is required"});
            }
            const folder = await Folder.findOne({_id:_id, userId: req.user.user_id});
            if(!folder){
                return res.status(400).send({error: "Folder doesn't exist."});
            }
            let result = await Folder.deleteOne({_id: _id})
            res.status(201).json({success: true});
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    getFolder: async(req, res)=>{
        try {
            const { before, after, perpage= 20 } = req.query;
            console.log("GetFolder", before, after, perpage,req.query)
            let query = {}
            if(before){
                let beforeDate = new Date(before)
                query = { userId: req.user.user_id , updatedAt: { $lt: beforeDate}}
            }else if(after){
                let afterDate = new Date(after)
                query = { userId: req.user.user_id , updatedAt: { $gt: afterDate}}
            }else{
                query = { userId: req.user.user_id }
            }
            let result = await Folder.find(query).sort({updatedAt: -1}).limit(perpage)
            let page = {first: result[0]?.updatedAt,last: result[result.length -1]?.updatedAt, count: result.length}
            result = result.map((item)=>{ 
                return {
                        _id: item._id,
                        name: item.name, 
                        description: item.description, 
                        createdAt: item.createdAt, 
                        updatedAt: item.updatedAt
                    }
            });
            res.status(201).json({page: page,data: result});
        } catch (error) {
            console.log("error", error)
            res.status(500).send({error: error});
        }
    }
}