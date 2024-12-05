import React, { useState } from 'react';
import API from "../api/api";

const RegisterForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [responseMessage, setResponseMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await API.post('/auth/register', {
                username: username,
                password: password,
            });

            if (response.status === 200) {
                setResponseMessage('Registracija sėkminga!');
            } else {
                setResponseMessage('Registracija nepavyko. Bandykite dar kartą.');
            }
        } catch (error) {
            console.error('Klaida registruojant vartotoją:', error);
            setResponseMessage('Registracija nepavyko. Bandykite dar kartą.');
        }
    };

    return (
        <div>
            <h2>Registracija</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Registruotis</button>
            </form>
            {responseMessage && <p>{responseMessage}</p>}
        </div>
    );
};

export default RegisterForm;
