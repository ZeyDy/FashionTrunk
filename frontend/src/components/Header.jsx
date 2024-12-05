import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../api/api";
import "../css/Header.css";

const Header = () => {
    const { user, setUser } = useContext(UserContext);
    const [categories, setCategories] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    const handleLogout = () => {
        localStorage.removeItem("user"); // Pašalinti naudotojo duomenis
        setUser(null); // Atnaujinti globalią būseną
        window.location.href = "/"; // Grįžti į pagrindinį puslapį
    };

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await API.get("/categories/allowed");
                setCategories(response.data);
            } catch (error) {
                console.error("Klaida gaunant kategorijas:", error);
            }
        };
        fetchCategories();
    }, []);

    const handleSearch = () => {
        if (searchTerm) {
            window.location.href = `/search?query=${searchTerm}`;
        }
    };

    return (
        <header className="header">
            <div className="header-left">
                <h1 className="logo">MyFashionTrunk</h1>
                <select className="category-dropdown">
                    {categories.map((category) => (
                        <option key={category}>{category}</option>
                    ))}
                </select>
                <input
                    type="text"
                    className="search-input"
                    placeholder="Search for products"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button onClick={handleSearch}>Search</button>
            </div>
            <div className="header-right">
                {!user ? (
                    <>
                        <Link to="/register" className="btn">
                            Register
                        </Link>
                        <Link to="/login" className="btn">
                            Log in
                        </Link>
                        <Link to="/admin" className="btn-primary">
                            Administratoriaus panelė
                        </Link>
                    </>
                ) : (
                    <>
                        <span>Hello, {user.username}!</span>
                        <button className="btn" onClick={handleLogout}>
                            Log out
                        </button>
                        <Link to="/adform" className="btn-primary">
                            Add an advertisement
                        </Link>
                        <Link to="/userads" className="btn-primary">
                            Your advertisements
                        </Link>
                        <Link to="/category-management" className="btn-primary">
                            Category Management
                        </Link>

                    </>
                )}
            </div>
        </header>
    );
};

export default Header;
