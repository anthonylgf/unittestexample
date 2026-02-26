# Unittestexample

## Deploying to AWS ECR

This project includes a shell script to build and deploy Docker images to Amazon Elastic Container Registry (ECR).

### Prerequisites

- Docker installed and running
- AWS CLI installed and configured with proper credentials
- Appropriate IAM permissions for ECR operations
- An existing ECR repository in your AWS account

### Usage

The deployment script is located at `script/aws/deploy-to-ecr.sh`.

**Make the script executable:**
```bash
chmod +x script/aws/deploy-to-ecr.sh
```

**Run the script:**

- You should execute the script from root folder.

```bash
./script/aws/deploy-to-ecr.sh --tag <tag> --awsuser <aws-account-id> --awsregion <region>
```