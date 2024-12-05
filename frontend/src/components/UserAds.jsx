import React, { useEffect, useState } from "react";
import API from "../api/api";
import "../css/AdsStyle.css";

const UserAds = () => {
    const [ads, setAds] = useState([]); // Skelbimų sąrašas
    const [message, setMessage] = useState(""); // Pranešimai naudotojui

    useEffect(() => {
        const fetchAds = async () => {
            try {
                const userString = localStorage.getItem("user");
                const user = JSON.parse(userString);

                if (!user || !user.userID) {
                    setMessage("Vartotojas neprisijungęs arba userId nerastas.");
                    return;
                }

                const response = await API.get(`/api/items/userads`, {
                    params: { userId: user.userID },
                });

                if (response.status === 200 && response.data.length > 0) {
                    setAds(response.data);
                    setMessage("");
                } else if (response.data.length === 0) {
                    setMessage("Neturite pridėtų skelbimų.");
                } else {
                    setMessage("Nepavyko gauti skelbimų.");
                }
            } catch (error) {
                console.error(
                    "Klaida gaunant skelbimus:",
                    error.response?.data || error.message
                );
                setMessage("Įvyko klaida gaunant skelbimus.");
            }
        };

        fetchAds();
    }, []);

    return (
        <div>
            <h2>My advertisements</h2>
            {message && ads.length === 0 && <p>{message}</p>}
            <div className="ads-container">
                {ads.map((ad) => (
                    <div className="ad-card" key={ad.adId}>
                        {ad.imageUrls && ad.imageUrls.length > 0 && (
                            <img src={ad.imageUrls[0]} alt={ad.title}/>
                        )}
                        <h3>{ad.title}</h3>
                        <p>Category: {ad.category}</p>
                        <p>Description: {ad.description}</p>
                        <p>Price: {ad.price} €</p>
                        <button>Update</button>
                        <button>Delete</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default UserAds;
