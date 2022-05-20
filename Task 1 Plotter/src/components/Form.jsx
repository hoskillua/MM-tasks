import React from 'react';

export default function Form(props) {
    const { setFormula, setMin, setMax, formulaError } = props;

    return (
        <div>
            <div className="form-column">
                <div className="row">
                    <label htmlFor="function">f(x)= </label>
                    <input
                        type="text"
                        name="function"
                        onChange={e => setFormula(e.target.value)}
                    />
                </div>
                {formulaError && (
                    <div className="error">Formula syntax is wrong</div>
                )}

                <div className="row">
                    <label htmlFor="function">min= </label>
                    <input
                        type="number"
                        name="min"
                        onChange={e => setMin(+e.target.value)}
                    />
                    <label htmlFor="function">max= </label>
                    <input
                        type="number"
                        name="max"
                        onChange={e => setMax(+e.target.value)}
                    />
                </div>
            </div>
        </div>
    );
}
