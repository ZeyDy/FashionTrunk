import React, { useState, useEffect } from "react";
import API from "../api/api"; // Pritaikykite prie savo API failo vietos
import "../css/Category.css";

const CategoryManagement = () => {
    const [prohibitedCategories, setProhibitedCategories] = useState({});
    const [allowedCategories, setAllowedCategories] = useState({});
    const [newCategoryName, setNewCategoryName] = useState("");
    const [newLabels, setNewLabels] = useState("");
    const [categoryType, setCategoryType] = useState("allowed"); // "allowed" arba "prohibited"

    // Gauti kategorijų sąrašus
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const prohibitedResponse = await API.get("/api/categories/prohibited");
                const allowedResponse = await API.get("/api/categories/allowed");
                setProhibitedCategories(prohibitedResponse.data);
                setAllowedCategories(allowedResponse.data);
            } catch (error) {
                console.error("Klaida gaunant kategorijas:", error);
            }
        };
        fetchCategories();
    }, []);

    // Pridėti naują kategoriją
    const handleAddCategory = async () => {
        const labelsArray = newLabels.split(",").map((label) => label.trim());
        const newCategory = { [newCategoryName]: labelsArray };

        try {
            if (categoryType === "allowed") {
                await API.post("/api/categories/allowed", newCategory);
            } else {
                await API.post("/api/categories/prohibited", newCategory);
            }
            alert("Kategorija sėkmingai pridėta!");
            window.location.reload(); // Perkraunama norint atnaujinti duomenis
        } catch (error) {
            console.error("Klaida pridedant kategoriją:", error);
            alert("Nepavyko pridėti kategorijos.");
        }
    };

    // Pašalinti kategoriją
    const handleDeleteCategory = async (type, categoryName) => {
        try {
            if (type === "allowed") {
                await API.delete(`/api/categories/allowed/${categoryName}`);
            } else {
                await API.delete(`/api/categories/prohibited/${categoryName}`);
            }
            alert("Kategorija sėkmingai pašalinta!");
            window.location.reload(); // Perkraunama norint atnaujinti duomenis
        } catch (error) {
            console.error("Klaida šalinant kategoriją:", error);
            alert("Nepavyko pašalinti kategorijos.");
        }
    };

    return (
        <div>
            <h1>Kategorijų Valdymas</h1>
            <div>
                <h2>Draudžiamos Kategorijos</h2>
                <ul>
                    {Object.entries(prohibitedCategories).map(([category, labels]) => (
                        <li key={category}>
                            <strong>{category}</strong>: {labels.join(", ")}
                            <button onClick={() => handleDeleteCategory("prohibited", category)}>
                                Pašalinti
                            </button>
                        </li>
                    ))}
                </ul>
            </div>

            <div>
                <h2>Leidžiamos Kategorijos</h2>
                <ul>
                    {Object.entries(allowedCategories).map(([category, labels]) => (
                        <li key={category}>
                            <strong>{category}</strong>: {labels.join(", ")}
                            <button onClick={() => handleDeleteCategory("allowed", category)}>
                                Pašalinti
                            </button>
                        </li>
                    ))}
                </ul>
            </div>

            <div>
                <h2>Pridėti Kategoriją</h2>
                <select value={categoryType} onChange={(e) => setCategoryType(e.target.value)}>
                    <option value="allowed">Leidžiama</option>
                    <option value="prohibited">Draudžiama</option>
                </select>
                <input
                    type="text"
                    placeholder="Kategorijos pavadinimas"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                />
                <textarea
                    placeholder="Žymės (atskirtos kableliais)"
                    value={newLabels}
                    onChange={(e) => setNewLabels(e.target.value)}
                ></textarea>
                <button onClick={handleAddCategory}>Pridėti</button>
            </div>
        </div>
    );
};

export default CategoryManagement;
