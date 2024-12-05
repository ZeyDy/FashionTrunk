import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; // Importuojame navigaciją
import API from "../api/api";
import "../css/FormStyle.css";

const AdForm = () => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [files, setFiles] = useState(null);
    const [message, setMessage] = useState("");
    const navigate = useNavigate(); // Naudojame navigaciją

    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append(
            "adRequest",
            JSON.stringify({
                title,
                description,
                price,
                userId: JSON.parse(localStorage.getItem("user")).userID,
            })
        );

        Array.from(files).forEach((file) => {
            formData.append("files", file);
        });

        try {
            const response = await API.post("/api/items/add", formData);
            if (response.status === 200) {
                setMessage("Skelbimas sėkmingai pridėtas!");

                // Nukreipiame vartotoją po 3 sekundžių
                setTimeout(() => {
                    navigate("/userads");
                }, 3000);
            }
        } catch (error) {
            setMessage("Klaida pridedant skelbimą.");
        }
    };

    return (
        <div className="form-container">
            <div className="form-content">
                <h2>Pridėti Skelbimą</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Pavadinimas"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                    <textarea
                        placeholder="Aprašymas"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    ></textarea>
                    <input
                        type="number"
                        placeholder="Kaina"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                    />
                    <input
                        type="file"
                        multiple
                        onChange={(e) => setFiles(e.target.files)}
                        required
                    />
                    <button type="submit">Pridėti</button>
                </form>
                {message && <p>{message}</p>}
            </div>
        </div>
    );
};

export default AdForm;
