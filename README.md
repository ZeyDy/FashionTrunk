# My Fashion Trunk

"My Fashion Trunk" is a socially conscious enterprise that manages an online platform enabling individuals to both sell and purchase fashion items. The platform moderates content to ensure adherence to a predefined set of permissible categories. This project leverages AI and cloud-native technologies to automate content moderation and improve scalability.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture Overview](#architecture-overview)
- [Setup Instructions](#setup-instructions)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Environment Variables](#environment-variables)
- [Usage](#usage)
- [Testing](#testing)
- [Folder Structure](#folder-structure)
- [API Documentation](#api-documentation)
- [License](#license)

---

## Project Overview

The platform uses **AWS DynamoDB** for data persistence, **AWS S3** for image storage, and **AWS Rekognition** for image analysis to classify and moderate items uploaded to the marketplace.

### Allowed and Prohibited Categories:

| Allowed Categories        | Prohibited Categories            |
|---------------------------|-----------------------------------|
| Fashion accessories       | Food products                   |
| All types of clothes       | Sports equipment                |
| All kinds of footwear      | Tobacco products                |
| Cosmetics                 | Cleaning supplies               |
| Children toys             | Weapons and armory              |
| Tech accessories          | Vehicles and automotive parts   |
| Pet care products         | Natural fur products            |

---

## Features

### User Features:
1. **Advertisement Management**:
   - Users can upload advertisements with images, including title, description, price, and category.
   - Users can edit and delete their advertisements.
2. **Automated Image Categorization**:
   - Uploaded images are analyzed with **AWS Rekognition**.
   - Automatically assigns categories or rejects prohibited content.

### Admin Features:
1. **Category Management Dashboard**:
   - Admins can add/edit/remove allowed and prohibited categories.
   - Admins can update category labels.

### Backend Features:
1. **DynamoDB Integration**:
   - Data persistence for user data, advertisements, and category configurations.
2. **AWS S3 Integration**:
   - Securely uploads and manages advertisement images.
3. **AWS Rekognition**:
   - Automatically detects and categorizes images based on predefined labels.

---

## Technologies Used

### Backend:
- **Java** (Spring Boot)
- **AWS DynamoDB** (Database)
- **AWS S3** (Storage)
- **AWS Rekognition** (Image Analysis)

### Frontend:
- **React** (JavaScript)
- **Axios** (HTTP client for API communication)

---

## Architecture Overview

The project architecture involves the following key components:

1. **Frontend**:
   - React-based Single Page Application (SPA) for users and admins.
2. **Backend**:
   - Spring Boot REST APIs for business logic, AWS services integration, and database operations.
3. **AWS Services**:
   - DynamoDB for storing user data and ads.
   - S3 for scalable image storage.
   - Rekognition for detecting and categorizing image content.

---

## Setup Instructions

### Backend Setup

1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd backend
## Setup Instructions

### Backend Setup

1. **Install dependencies**:
   ```bash
   ./mvnw clean install

2. **Configure environment variables: See the Environment Variables section for details.**

3. **Run the Spring Boot server**:

bash
Copy code
./mvnw spring-boot:run


***Frontend Setup***
**Navigate to the frontend directory**:

bash
Copy code
cd frontend
**Install dependencies**:

bash
Copy code
npm install
**Start the development server**:

bash
Copy code
npm start
***Environment Variables***
**Backend (application.properties)**:
properties
Copy code
aws.accessKeyId=YOUR_ACCESS_KEY_ID
aws.secretAccessKey=YOUR_SECRET_ACCESS_KEY
aws.region=YOUR_REGION
aws.bucketName=YOUR_BUCKET_NAME
aws.s3.endpoint-url=YOUR_S3_ENDPOINT_URL

## Usage

### Run the Backend:
Start the Spring Boot backend server.

### Run the Frontend:
Start the React development server.

### Access the Application:
Open [http://localhost:3000](http://localhost:3000) in your browser.

## Features
- Upload advertisements (with images).
- Edit/Delete existing advertisements.
- Admin dashboard to manage categories.

## Testing

### Backend Testing:
Use the following command to run unit and integration tests:
```bash
./mvnw test


### License
This project is licensed under the MIT License.