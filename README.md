# 📘 DangerZone Backend -- API Endpoints

Este documento descreve todos os endpoints identificados no backend do
**DangerZone**, incluindo rotas de autenticação, relatórios e heatmap.

## 🌐 Base URL

http://20.206.241.188:8080

# 🔐 Autenticação

## POST /auth/register

Cria um novo usuário.

### Body

``` json
{
  "email": "john.doe@test.com",
  "fullName": "John John",
  "password": "123456"
}
```

### Resposta

``` json
{"message":"User registered successfully"}
```

## POST /auth/login

Retorna um token JWT.

### Body

``` json
{
  "email": "example@example.com",
  "password": "123456"
}
```

### Resposta

``` json
{"token":"JWT_TOKEN_HERE"}
```

## GET /auth/profile

Requer JWT.

### Resposta

``` json
{
  "id": 1,
  "email": "john@test.com",
  "fullName": "John Doe"
}
```

## PUT /auth/change-data

Atualiza os dados do usuário autenticado.

### Body

``` json
{
  "newEmail": "test@test.com",
  "newFullName": "John Dove"
}
```

# 📝 Relatórios

## POST /api/reports

Cria um novo relatório.

### Body

``` json
{
  "anonymous": true,
  "crimeType": "Homicídio",
  "description": "Descrição do ocorrido...",
  "latitude": -23.5489,
  "longitude": -46.6388,
  "cidade": "São Paulo",
  "estado": "SP"
}
```

# 🗺 Heatmap

## GET /heatmap

Retorna pontos para o mapa de calor.

### Resposta

``` json
[
  {"latitude": -23.5489, "longitude": -46.6388, "crimeType": "Homicídio"}
]
```
