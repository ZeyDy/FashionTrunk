import React, { useState } from "react";
import "../css/ModalOverlay.css";

const EditAdForm = ({ ad, onSubmit, onClose }) => {
    const [title, setTitle] = useState(ad.title);
    const [description, setDescription] = useState(ad.description);
    const [price, setPrice] = useState(ad.price);
    const [newImages, setNewImages] = useState([]);

    const handleFileChange = (e) => {
        setNewImages(e.target.files);
    };

    const handleSubmit = (e) => {
        e.preventDefault(); // Užkirsti kelią numatytajai naršyklės veiksenai

        // Sukuriame formData objektą
        const formData = new FormData();
        formData.append("adRequest", JSON.stringify({ title, description, price }));
        for (let i = 0; i < newImages.length; i++) {
            formData.append("files", newImages[i]);
        }

        console.log("Submitting form with data: ", { title, description, price, newImages });
        onSubmit(ad.adId, formData); // Kvietimas tėvinės komponento funkcijos
    };

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <div className="modal-header">
                    <h2>Redaguoti skelbimą</h2>
                    <button className="close-button" onClick={onClose}>
                        &times;
                    </button>
                </div>
                <div className="modal-body">
                    <form onSubmit={handleSubmit}>
                        <label>
                            Pavadinimas:
                            <input
                                type="text"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                required
                            />
                        </label>
                        <label>
                            Aprašymas:
                            <textarea
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                required
                            />
                        </label>
                        <label>
                            Kaina:
                            <input
                                type="number"
                                value={price}
                                onChange={(e) => setPrice(e.target.value)}
                                required
                            />
                        </label>
                        <label>
                            Naujos nuotraukos:
                            <input type="file" multiple onChange={handleFileChange} />
                        </label>
                        <div className="modal-footer">
                            <button type="button" onClick={onClose}>
                                Uždaryti
                            </button>
                            <button type="submit">Išsaugoti</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default EditAdForm;