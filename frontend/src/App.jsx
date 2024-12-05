import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./components/Header";
import ContentSection from "./components/ContentSection";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import AdminPanel from "./components/AdminPanel";
import AdForm from "./components/AdForm";
import UserAds from "./components/UserAds";
import CategoryManagement from "./components/CategoryManagement";

const App = () => {
    return (
        <Router>
            <Header />
            <Routes>
                {/* Pagrindinis puslapis */}
                <Route path="/" element={<ContentSection />} />

                {/* Prisijungimo puslapis */}
                <Route path="/login" element={<LoginForm />} />

                {/* Registracijos puslapis */}
                <Route path="/register" element={<RegisterForm />} />

                {/* Administratoriaus panelė */}
                <Route path="/admin" element={<AdminPanel />} />

                <Route path="/adform" element={<AdForm />} />

                <Route path="/userads" element={<UserAds />} />

                <Route path="/category-management" element={<CategoryManagement />} />
            </Routes>
        </Router>
    );
};

export default App;
