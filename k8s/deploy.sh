#!/bin/bash

set -e

echo "======================================"
echo "PetFriends - Deploy to Kubernetes"
echo "======================================"

echo "Creating namespace..."
kubectl apply -f namespace.yaml

echo "Deploying PostgreSQL..."
kubectl apply -f postgres/postgres-secret.yaml
kubectl apply -f postgres/postgres-configmap.yaml
kubectl apply -f postgres/postgres-pvc.yaml
kubectl apply -f postgres/postgres-statefulset.yaml
kubectl apply -f postgres/postgres-service.yaml

echo "Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n petfriends --timeout=300s

echo "Deploying Zookeeper..."
kubectl apply -f kafka/zookeeper-deployment.yaml
kubectl apply -f kafka/zookeeper-service.yaml

echo "Waiting for Zookeeper to be ready..."
kubectl wait --for=condition=ready pod -l app=zookeeper -n petfriends --timeout=180s

echo "Deploying Kafka..."
kubectl apply -f kafka/kafka-deployment.yaml
kubectl apply -f kafka/kafka-service.yaml

echo "Waiting for Kafka to be ready..."
kubectl wait --for=condition=ready pod -l app=kafka -n petfriends --timeout=300s

echo "Deploying Zipkin..."
kubectl apply -f zipkin/zipkin-deployment.yaml
kubectl apply -f zipkin/zipkin-service.yaml

echo "Waiting for Zipkin to be ready..."
kubectl wait --for=condition=ready pod -l app=zipkin -n petfriends --timeout=180s

echo "Deploying Elasticsearch..."
kubectl apply -f elasticsearch/elasticsearch-statefulset.yaml
kubectl apply -f elasticsearch/elasticsearch-service.yaml

echo "Waiting for Elasticsearch to be ready..."
kubectl wait --for=condition=ready pod -l app=elasticsearch -n petfriends --timeout=300s

echo "Deploying Logstash..."
kubectl apply -f logstash/logstash-configmap.yaml
kubectl apply -f logstash/logstash-deployment.yaml
kubectl apply -f logstash/logstash-service.yaml

echo "Waiting for Logstash to be ready..."
kubectl wait --for=condition=ready pod -l app=logstash -n petfriends --timeout=180s

echo "Deploying Kibana..."
kubectl apply -f kibana/kibana-deployment.yaml
kubectl apply -f kibana/kibana-service.yaml

echo "Waiting for Kibana to be ready..."
kubectl wait --for=condition=ready pod -l app=kibana -n petfriends --timeout=300s

echo "Deploying Almoxarifado microservice..."
kubectl apply -f almoxarifado/almoxarifado-configmap.yaml
kubectl apply -f almoxarifado/almoxarifado-deployment.yaml
kubectl apply -f almoxarifado/almoxarifado-service.yaml
kubectl apply -f almoxarifado/almoxarifado-hpa.yaml

echo "Deploying Transporte microservice..."
kubectl apply -f transporte/transporte-configmap.yaml
kubectl apply -f transporte/transporte-deployment.yaml
kubectl apply -f transporte/transporte-service.yaml
kubectl apply -f transporte/transporte-hpa.yaml

echo ""
echo "======================================"
echo "Deployment completed successfully!"
echo "======================================"
echo ""
echo "Check status with:"
echo "  kubectl get pods -n petfriends"
echo "  kubectl get services -n petfriends"
echo ""
echo "Access services:"
echo "  Almoxarifado: kubectl port-forward svc/almoxarifado-service 8082:80 -n petfriends"
echo "  Transporte: kubectl port-forward svc/transporte-service 8083:80 -n petfriends"
echo "  Zipkin: kubectl port-forward svc/zipkin-service 9411:9411 -n petfriends"
echo "  Kibana: kubectl port-forward svc/kibana-service 5601:5601 -n petfriends"
echo ""

