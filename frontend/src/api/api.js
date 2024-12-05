import axios from 'axios';

const API = axios.create({
    baseURL: 'http://localhost:8080', // Spring Boot API URL
});

export default API;

export const uploadImage = (formData) => API.post('/images/upload', formData);

export const getAllowedCategories = () => API.get('/categories/allowed');
export const getProhibitedCategories = () => API.get('/categories/prohibited');
