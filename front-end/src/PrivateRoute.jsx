import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export function PrivateRoute({ children, allowedRoles }) {
  const { user } = useAuth();

  if (!user || !user.tipo || !allowedRoles.includes(user.tipo)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
