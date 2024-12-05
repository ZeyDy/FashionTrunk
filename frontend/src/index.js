import React from 'react';
import ReactDOM from 'react-dom/client'; // Importuojame iš 'react-dom/client'
import App from './App';
import {UserProvider} from "./components/UserContext";

const root = ReactDOM.createRoot(document.getElementById('root')); // Sukuriame root

root.render(
    <React.StrictMode>
        <UserProvider>
            <App />
        </UserProvider>
    </React.StrictMode>
);
