#!/bin/bash

export AWS_ACCESS_KEY_ID=000000000000 AWS_SECRET_ACCESS_KEY=000000000000

echo "Criando Segredos no AWS Secret Manager do LocalStack..."

awslocal secretsmanager create-secret --name /secret/unittestexample --description "Segredos para acesso ao banco de dados" --secret-string "{\"db-username\":\"postgres\",\"db-password\":\"root\"}"
