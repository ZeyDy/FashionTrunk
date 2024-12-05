import React from "react";
import "../css/ModalOverlay.css";

const ConfirmDeleteModal = ({ onConfirm, onCancel }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <div className="modal-header">
                    <h2>Patvirtinti ištrynimą</h2>
                    <button className="close-button" onClick={onCancel}>
                        &times;
                    </button>
                </div>
                <div className="modal-body">
                    <p>Ar tikrai norite ištrinti šį skelbimą?</p>
                </div>
                <div className="modal-footer">
                    <button className="btn" onClick={onCancel}>
                        Ne
                    </button>
                    <button className="btn btn-danger" onClick={onConfirm}>
                        Taip
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmDeleteModal;
