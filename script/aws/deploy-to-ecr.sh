#!/bin/bash

# Parse command line arguments
TAG=""
AWS_USER=""
AWS_REGION=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --tag)
            TAG="$2"
            shift 2
            ;;
        --awsuser)
            AWS_USER="$2"
            shift 2
            ;;
        --awsregion)
            AWS_REGION="$2"
            shift 2
            ;;
        *)
            echo "Unknown parameter: $1"
            exit 1
            ;;
    esac
done

# Validate required parameters
if [ -z "$TAG" ] || [ -z "$AWS_USER" ] || [ -z "$AWS_REGION" ]; then
    echo "Error: Missing required parameters"
    echo "Usage: ./deploy-to-ecr.sh --tag <tag> --awsuser <aws-account-id> --awsregion <region>"
    exit 1
fi

# Configuration (update these values)
REPOSITORY_NAME="unittestexample"
IMAGE_NAME="unittestexample"
ECR_URL="${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com"

echo "Building Docker image..."
docker build -t ${IMAGE_NAME}:${TAG} .

if [ $? -ne 0 ]; then
    echo "Error: Docker build failed"
    exit 1
fi

echo "Logging into ECR..."
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URL}

if [ $? -ne 0 ]; then
    echo "Error: ECR login failed"
    exit 1
fi

echo "Tagging image for ECR..."
docker tag ${IMAGE_NAME}:${TAG} ${ECR_URL}/${REPOSITORY_NAME}:${TAG}

if [ $? -ne 0 ]; then
    echo "Error: Docker tag failed"
    exit 1
fi

echo "Pushing image to ECR..."
docker push ${ECR_URL}/${REPOSITORY_NAME}:${TAG}

if [ $? -ne 0 ]; then
    echo "Error: Docker push failed"
    exit 1
fi

echo "Successfully deployed ${IMAGE_NAME}:${TAG} to ECR!"
echo "Image URI: ${ECR_URL}/${REPOSITORY_NAME}:${TAG}"