db = db.getSiblingDB('auth');

db.usuarios.insertMany([
    {
        email: "admin@airport.com",
        senha: "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", // admin
        tipo: "FUNCIONARIO",
        ativo: true
    },
    {
        email: "cliente1@email.com",
        senha: "481f6cc0511143ccdd7e2d1b1b94faf0a700a8b49cd13922a70b5ae28acaa8c5", // cliente123
        tipo: "CLIENTE",
        ativo: true
    },
    {
        email: "funcionario1@airport.com",
        senha: "d82494f05d6917ba02f7aaa29689ccb444bb73f20380876cb05d1f37537b7892", // func123
        tipo: "FUNCIONARIO",
        ativo: true
    },
    {
        email: "cliente2@email.com",
        senha: "481f6cc0511143ccdd7e2d1b1b94faf0a700a8b49cd13922a70b5ae28acaa8c5", // cliente123
        tipo: "CLIENTE",
        ativo: false
    }
]);

// Create unique index for email
db.usuarios.createIndex({ "email": 1 }, { unique: true }); 