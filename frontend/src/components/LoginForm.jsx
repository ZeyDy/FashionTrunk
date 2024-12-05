import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom"; // Įtraukiame useNavigate
import API from "../api/api";
import { UserContext } from "./UserContext";

const LoginForm = () => {
    const { setUser } = useContext(UserContext);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate(); // Sukuriame navigacijos funkciją

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await API.post("/auth/login", {
                username,
                password,
            });

            if (response.status === 200) {
                const userData = {
                    username: response.data.username,
                    userID: response.data.userId,
                    token: response.data.token,
                };
                console.log("Saugomi duomenys:", userData);

                localStorage.setItem("user", JSON.stringify(userData));
                setUser(userData); // Atnaujina globalią būseną
                setMessage("Prisijungimas sėkmingas!");

                // Peradresuojame į pagrindinį puslapį
                navigate("/");
            }
        } catch (error) {
            setMessage("Prisijungimas nepavyko. Patikrinkite duomenis.");
        }
    };

    return (
        <div className="form-container">
            <div className="form-content">
            <h2>Prisijungimas</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button type="submit">Prisijungti</button>
            </form>
            {message && <p>{message}</p>}
            </div>
        </div>
    );
};

export default LoginForm;
