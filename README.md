# 📝 Serverless OCR Text Extractor (AWS Textract)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![AWS Lambda](https://img.shields.io/badge/AWS%20Lambda-Serverless-yellow?logo=aws-lambda)](https://aws.amazon.com/lambda/)
[![API Gateway](https://img.shields.io/badge/API%20Gateway-REST-orange?logo=amazon-aws)](https://aws.amazon.com/api-gateway/)
[![S3](https://img.shields.io/badge/S3-Storage-569A31?logo=amazon-aws&logoColor=white)](https://aws.amazon.com/s3/)
[![AWS Textract](https://img.shields.io/badge/AWS%20Textract-OCR-0972D5?logo=amazon-aws&logoColor=white)](https://aws.amazon.com/textract/)
[![AWS SAM](https://img.shields.io/badge/SAM-IaC-FF9900?logo=aws)](https://aws.amazon.com/serverless/sam/)


> Many businesses still deal with scanned documents, invoices, and receipts. This app provides a way to extract text from those documents using AWS Textract. Upload a file and get structured text data back — useful for automating paperwork and bookkeeping.

---

## 📚 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Live Demo](#-live-demo)
- [API Endpoints](#-api-endpoints)
- [Deployment (via SAM)](#-deployment-via-sam)
- [Screenshots](#-screenshots)
- [Possible Improvements](#-possible-improvements)
- [Contact](#-contact)

---

## 🛠️ Tech Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **Cloud Services:**
    - **AWS Lambda** – for serverless compute
    - **Amazon API Gateway** – REST API endpoints
    - **Amazon S3** – Image storage
    - **AWS Textract** – OCR text extraction
    - **AWS SAM** – infrastructure as code (IaC)

---

## 📌 Features

- 🔐 Pre-signed URL generation to securely upload images
- 📦 Lambda auto-triggered via S3 event notifications
- 📄 Text extracted from image using Textract’s `DetectDocumentText`

---

## 🧭 Live Demo

![Live Demo](assets/live-demo.gif)

---

## 🔌 API Endpoints

### `POST /pre-signed-url`
Generate a pre-signed URL to upload a file.

- **Request Body:**
```json
{
  "fileType": "jpg"
}
```
- **Success Response:**
```json
{
  "uploadUrl": "https://s3-url",
  "key": "uploads/xyz.jpg"
}
```

---

## 🚀 Deployment (via SAM)

```bash
# 1. Build
sam build

# 2. Deploy
sam deploy --guided

```
---

## 🖼️ Screenshots

### 🚀 API Gateway
![API Gateway](assets/aws-api-gateway.PNG)

### ✅ Lambda - GeneratePreSignedUrlFunction
![Lambda GeneratePreSignedUrlFunction](assets/aws-lambda-presigned.PNG)

### 📦 Lambda - ProcessImageFunction
![Lambda - ProcessImageFunction](assets/aws-lambda-process-image.PNG)

---

## 🧭 Possible Improvements

- 📝 Store results in DynamoDB or S3
- 🧪 Add API to fetch OCR results

---

## 📬 Contact

Built by **Kamran Zeynalov**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin&style=flat-square)](https://www.linkedin.com/in/zeynalov-kamran/)

