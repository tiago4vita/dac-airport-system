import React from "react";
import "./style.css";

import Group from "./assets/Group.svg";

export const TelaInicialCli = () => {
    return (
        <div className = "telaInicialCli">
            <div className="div">
                <div className="rectangle-1">
                    <img className="group-icon" alt="icon" src={Group}/>
                    <div className="text-wrapper">DAC Aéreo</div>
                </div>
                <div className="rectangle-2">
                <div className="text-wrapper-2">Saldo Atual</div>
                </div>

                <div>
                    <table class="border-collapse border border-gray-400">
                    <thead>
                        <tr>
                            <th class="border border-gray-300">Código</th>
                            <th class="border border-gray-300">Origem</th>
                            <th class="border border-gray-300">Destino</th>
                            <th class="border border-gray-300">Data</th>
                            <th class="border border-gray-300">Hora</th>
                            <th class="border border-gray-300">Status</th>
                            <th class="border border-gray-300">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="border border-gray-300">Indiana</td>
                            <td class="border border-gray-300">Indianapolis</td>
                            <td class="border border-gray-300">Indianapolis</td>
                            <td class="border border-gray-300">Indianapolis</td>
                            <td class="border border-gray-300">Indianapolis</td>
                            <td class="border border-gray-300">Indianapolis</td>
                            <td class="border border-gray-300">Indianapolis</td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">Ohio</td>
                            <td class="border border-gray-300">Columbus</td>
                            <td class="border border-gray-300">Columbus</td>
                            <td class="border border-gray-300">Columbus</td>
                            <td class="border border-gray-300">Columbus</td>
                            <td class="border border-gray-300">Columbus</td>
                            <td class="border border-gray-300">Columbus</td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">Michigan</td>
                            <td class="border border-gray-300">Detroit</td>
                            <td class="border border-gray-300">Detroit</td>
                            <td class="border border-gray-300">Detroit</td>
                            <td class="border border-gray-300">Detroit</td>
                            <td class="border border-gray-300">Detroit</td>
                            <td class="border border-gray-300">Detroit</td>
                        </tr>
                    </tbody>
                    </table>
                </div>
        </div>
        </div>
    );
};