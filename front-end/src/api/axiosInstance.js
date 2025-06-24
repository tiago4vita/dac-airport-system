 import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:3001",
});

 api.interceptors.request.use(
   (config) => {
     const token = sessionStorage.getItem("token");
     if (token) {
       config.headers["Authorization"] = `Bearer ${token}`;
     }
     return config;
   },
   (error) => {
     return Promise.reject(error);
   }
 );

 export default api;
