#!/bin/bash

set -e

echo "======================================"
echo "PetFriends - Undeploy from Kubernetes"
echo "======================================"

echo "Deleting microservices..."
kubectl delete -f transporte/transporte-hpa.yaml --ignore-not-found=true
kubectl delete -f transporte/transporte-service.yaml --ignore-not-found=true
kubectl delete -f transporte/transporte-deployment.yaml --ignore-not-found=true
kubectl delete -f transporte/transporte-configmap.yaml --ignore-not-found=true

kubectl delete -f almoxarifado/almoxarifado-hpa.yaml --ignore-not-found=true
kubectl delete -f almoxarifado/almoxarifado-service.yaml --ignore-not-found=true
kubectl delete -f almoxarifado/almoxarifado-deployment.yaml --ignore-not-found=true
kubectl delete -f almoxarifado/almoxarifado-configmap.yaml --ignore-not-found=true

echo "Deleting Kibana..."
kubectl delete -f kibana/kibana-service.yaml --ignore-not-found=true
kubectl delete -f kibana/kibana-deployment.yaml --ignore-not-found=true

echo "Deleting Logstash..."
kubectl delete -f logstash/logstash-service.yaml --ignore-not-found=true
kubectl delete -f logstash/logstash-deployment.yaml --ignore-not-found=true
kubectl delete -f logstash/logstash-configmap.yaml --ignore-not-found=true

echo "Deleting Elasticsearch..."
kubectl delete -f elasticsearch/elasticsearch-service.yaml --ignore-not-found=true
kubectl delete -f elasticsearch/elasticsearch-statefulset.yaml --ignore-not-found=true

echo "Deleting Zipkin..."
kubectl delete -f zipkin/zipkin-service.yaml --ignore-not-found=true
kubectl delete -f zipkin/zipkin-deployment.yaml --ignore-not-found=true

echo "Deleting Kafka..."
kubectl delete -f kafka/kafka-service.yaml --ignore-not-found=true
kubectl delete -f kafka/kafka-deployment.yaml --ignore-not-found=true
kubectl delete -f kafka/zookeeper-service.yaml --ignore-not-found=true
kubectl delete -f kafka/zookeeper-deployment.yaml --ignore-not-found=true

echo "Deleting PostgreSQL..."
kubectl delete -f postgres/postgres-service.yaml --ignore-not-found=true
kubectl delete -f postgres/postgres-statefulset.yaml --ignore-not-found=true
kubectl delete -f postgres/postgres-pvc.yaml --ignore-not-found=true
kubectl delete -f postgres/postgres-configmap.yaml --ignore-not-found=true
kubectl delete -f postgres/postgres-secret.yaml --ignore-not-found=true

echo "Deleting namespace..."
kubectl delete namespace petfriends --ignore-not-found=true

echo ""
echo "======================================"
echo "Undeploy completed successfully!"
echo "======================================"

