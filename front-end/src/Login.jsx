import React from "react";
 import "./style.css";
 
 import vector from "./assets/vector.svg";
 import group4174 from "./assets/group-1000004174.png"; // imagem que substitui tudo
 
 export const Login = () => {
   return (
     <div className="login">
       <div className="div">
         <img className="vector-bg" alt="Vector" src={vector} />
 
         <div className="text-wrapper">Login</div>
 
         <div className="group-image-wrapper">
           <img
             className="group-image"
             src={group4174}
             alt="Ilustração de interface"
           />
         </div>
 
         <div className="text-wrapper-2">Email</div>
         <div className="placeholder">
           <div className="frame">
             <input
               type="email"
               className="text-wrapper-5"
               placeholder="Email"
             />
           </div>
         </div>
 
         <div className="text-wrapper-3">Senha</div>
         <div className="placeholder-2">
           <div className="frame">
             <input
               type="password"
               className="text-wrapper-6"
               placeholder="Senha"
             />
           </div>
         </div>
 
         <p className="n-o-possui-uma-conta">
           <span className="span">Não possui uma conta? </span>
           <a className="text-wrapper-4" href="#">Cadastre-se!</a>
         </p>
 
         <button className="button">
           <div className="text-wrapper-7">Login</div>
         </button>
       </div>
     </div>
   );
 };