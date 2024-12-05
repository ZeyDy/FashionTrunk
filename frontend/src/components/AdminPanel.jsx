import React, { useEffect, useState } from "react";
import API from "../api/api";

const AdminPanel = () => {
    const [items, setItems] = useState([]);

    useEffect(() => {
        const fetchItems = async () => {
            try {
                const response = await API.get("/api/items");
                setItems(response.data);
            } catch (error) {
                console.error("Klaida gaunant skelbimus:", error);
            }
        };
        fetchItems();
    }, []);

    const updateStatus = async (id, newStatus) => {
        try {
            await API.put(`/api/items/${id}`, null, {
                params: { status: newStatus },
            });
            setItems((prev) =>
                prev.map((item) =>
                    item.itemID === id ? { ...item, status: newStatus } : item
                )
            );
        } catch (error) {
            console.error("Nepavyko atnaujinti statuso:", error);
        }
    };

    return (
        <div>
            <h2>Administratoriaus Panelė</h2>
            <ul>
                {items.map((item) => (
                    <li key={item.itemID}>
                        <img src={item.imageUrl} alt={item.category} width="100" />
                        <p>Kategorija: {item.category}</p>
                        <p>Statusas: {item.status}</p>
                        <button onClick={() => updateStatus(item.itemID, "Approved")}>
                            Patvirtinti
                        </button>
                        <button onClick={() => updateStatus(item.itemID, "Rejected")}>
                            Atmesti
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default AdminPanel;
