db = db.getSiblingDB('auth');

db.usuarios.insertMany([
  {
    "_id": "a3773666-379c-4273-ab10-22632f56d21b",
    "login": "joaomelara@xerocopiadora.com.br",
    "senha": "e02d677612697a6e5edd098a1899ff98191504a11b735875fc87e9c1822c2581",
    "tipo": "CLIENTE",
    "ativo": true,
    "_class": "airportsystem.msauth.model.Usuario"
  },
  {
    "_id": "7b9a4f9c-b01c-4733-bb64-f848f0d76952",
    "login": "func_pre@gmail.com",
    "senha": "51e97818fca8cc936080e045f50ca32812fc364f3f68fe27798e9c15a2014dd5",
    "tipo": "FUNCIONARIO",
    "ativo": true,
    "_class": "airportsystem.msauth.model.Usuario"
  }
]); 