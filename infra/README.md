# Automating EC2 Instance creation and configuration using Terraform and Ansible

## Usage

### Ensure installed dependencies:

- [Terraform](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)
- [Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)


### Download SSH private key from AWS Academy
1. Save to preferred location
2. Open terraform.tfvars in the terraform directory 
3. Change the value of the variable `ssh_private_key` to point to your SSH private key
--- 

### Configure AWS CLI
```bash
aws configure

# Enter your AWS Access Key ID
# Enter your AWS Secret Access Key
# Enter your default region name
```

---

### Export AWS credentials
```bash
export AWS_ACCESS_KEY_ID=your_access_key_id
export AWS_SECRET_ACCESS_KEY=your_secret_access_key
export AWS_SESSION_TOKEN=your_session_token 
export AWS_DEFAULT_REGION=your_default_region
```

---

### Create and configure EC2 instance
```bash
cd terraform
terraform init
terraform apply
```
---
