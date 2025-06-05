# 🚀 Deploy LectureBot with Helm

This guide explains how to use Helm to deploy the `lecturebot` application to a Kubernetes cluster (e.g., locally via `kubectl` or remotely to AET) using the `team-lecture-bot` namespace.

---

## 📦 Prerequisites

- [Helm 3+](https://helm.sh/docs/intro/install/)
- Access to your Kubernetes cluster (`kubectl config current-context` should return 'student'))
- Namespace `team-lecture-bot` (created automatically if missing)

---

## 🚀 Install or Upgrade the App

To deploy or upgrade the `lecturebot` app using your Helm chart:

### Option 1: Using upgrade (installs if not present, upgrades if present)
```bash
helm upgrade --install lecturebot ./helm/lecture-bot-app \
  --namespace team-lecture-bot \
  --create-namespace \
  --values ./helm/lecture-bot-app/values.yaml
```

### Option 2: Using install (fails if already installed)
```bash
helm install lecturebot ./helm/lecture-bot-app \
  --namespace team-lecture-bot \
  --create-namespace \
  --values ./helm/lecture-bot-app/values.yaml
```

### 🛠️ Uninstall the App
To uninstall the `lecturebot` app, run:
```bash
helm uninstall lecturebot --namespace team-lecture-bot
```

### 🔍 Verify Deployment
To check if the `lecturebot` app is running, use:
```bash
kubectl get pods -n team-lecture-bot
```


