import './styles/styles.css';
import React from 'react';
import Form from './components/Form';
import PlotComp from './components/Plot';

export default function App() {
    const [formula, setFormula] = React.useState('x**2');
    const [min, setMin] = React.useState(-2);
    const [max, setMax] = React.useState(2);
    const [formulaError, setFormulaError] = React.useState(false);

    return (
        <div>
            <div className="page">
                <Form
                    setFormula={setFormula}
                    setMin={setMin}
                    setMax={setMax}
                    formula={formula}
                    min={min}
                    max={max}
                    formulaError={formulaError}
                />
                <PlotComp
                    formula={formula}
                    min={min}
                    max={max}
                    setFormulaError={setFormulaError}
                />
            </div>
        </div>
    );
}
