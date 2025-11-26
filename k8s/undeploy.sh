#!/bin/bash

echo "======================================"
echo "PetFriends - Undeploy from Kubernetes"
echo "======================================"
echo ""

# Verificar se o namespace existe
if ! kubectl get namespace petfriends &> /dev/null; then
    echo "Namespace 'petfriends' nao existe. Nada a fazer."
    exit 0
fi

echo "Deletando recursos do namespace petfriends..."
echo ""

# Deletar recursos na ordem reversa do deploy para evitar dependências

echo "1. Deletando HPAs..."
kubectl delete hpa --all -n petfriends --ignore-not-found=true --timeout=30s

echo "2. Deletando Deployments dos microsserviços..."
kubectl delete deployment almoxarifado transporte -n petfriends --ignore-not-found=true --timeout=30s

echo "3. Deletando Services dos microsserviços..."
kubectl delete service almoxarifado-service almoxarifado-internal -n petfriends --ignore-not-found=true
kubectl delete service transporte-service transporte-internal -n petfriends --ignore-not-found=true

echo "4. Deletando ConfigMaps dos microsserviços..."
kubectl delete configmap almoxarifado-config transporte-config -n petfriends --ignore-not-found=true

echo "5. Deletando Deployments de observabilidade..."
kubectl delete deployment kibana logstash zipkin -n petfriends --ignore-not-found=true --timeout=30s

echo "6. Deletando Services de observabilidade..."
kubectl delete service kibana-service logstash-service zipkin-service -n petfriends --ignore-not-found=true

echo "7. Deletando ConfigMaps de observabilidade..."
kubectl delete configmap logstash-config -n petfriends --ignore-not-found=true

echo "8. Deletando StatefulSets..."
kubectl delete statefulset elasticsearch postgres -n petfriends --ignore-not-found=true --timeout=30s

echo "9. Deletando Services de infraestrutura..."
kubectl delete service elasticsearch-service postgres-service -n petfriends --ignore-not-found=true

echo "10. Deletando Kafka e Zookeeper..."
kubectl delete deployment kafka zookeeper -n petfriends --ignore-not-found=true --timeout=30s
kubectl delete service kafka-service zookeeper-service -n petfriends --ignore-not-found=true

echo "11. Deletando PVCs..."
kubectl delete pvc --all -n petfriends --ignore-not-found=true --timeout=30s

echo "12. Deletando ConfigMaps e Secrets restantes..."
kubectl delete configmap postgres-config -n petfriends --ignore-not-found=true
kubectl delete secret postgres-secret -n petfriends --ignore-not-found=true

echo ""
echo "13. Deletando namespace..."
kubectl delete namespace petfriends --timeout=60s &

# PID do comando delete
DELETE_PID=$!

# Aguardar com feedback visual
echo "Aguardando delecao (pode levar ate 90 segundos)..."
for i in {1..45}; do
    if ! kubectl get namespace petfriends &> /dev/null; then
        echo ""
        echo "Namespace deletado com sucesso!"
        wait $DELETE_PID 2>/dev/null
        break
    fi
    echo -n "."
    sleep 2
    
    # Se já passou dos 90 segundos, forçar
    if [ $i -eq 45 ]; then
        echo ""
        echo ""
        echo "Tempo esgotado. Forcando delecao..."
        kill $DELETE_PID 2>/dev/null || true
        wait $DELETE_PID 2>/dev/null || true
        
        # Tentar forçar deleção
        kubectl delete namespace petfriends --force --grace-period=0 2>/dev/null || true
        sleep 5
    fi
done

echo ""

# Verificar resultado final
if kubectl get namespace petfriends &> /dev/null; then
    echo ""
    echo "AVISO: Namespace ainda existe. Pode estar com recursos presos."
    echo ""
    echo "Recursos remanescentes:"
    kubectl get all -n petfriends 2>/dev/null || echo "  (nenhum recurso visivel)"
    echo ""
    echo "Para forcar remocao completa, execute:"
    echo "  kubectl delete namespace petfriends --force --grace-period=0"
    echo "  kubectl patch namespace petfriends -p '{\"metadata\":{\"finalizers\":[]}}' --type=merge"
    echo ""
    exit 1
fi

echo ""
echo "======================================"
echo "Undeploy completed successfully!"
echo "======================================"
echo ""

