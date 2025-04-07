import React from "react";
import "./TelaInicialCli.css";

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
                    <div className="text-wrapper-3">1.5000</div>
                </div>
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
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                        <tr>
                            <td class="border border-gray-300">XTR945</td>
                            <td class="border border-gray-300">YTZ</td>
                            <td class="border border-gray-300">GRU</td>
                            <td class="border border-gray-300">02/07/2025</td>
                            <td class="border border-gray-300">02:50</td>
                            <td class="border border-gray-300">CRIADA</td>
                            <td class="border border-gray-300"></td>
                        </tr>
                    </tbody>
                    </table>
            </div>
        </div>
    );
};