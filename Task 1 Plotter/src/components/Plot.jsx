import React, { useEffect } from 'react';
import Plot from 'react-plotly.js';

export default function PlotComp(props) {
    const { formula, min, max, setFormulaError } = props;
    const [data, setData] = React.useState({ x: [], y: [] });
    useEffect(() => {
        console.log(formula, min, max);
        let x = [];
        let y = [];
        let valid = true;
        const checkFormula = str => {
            const re =
                /(?:(?:^|[-+_*/])(?:\s*-?\d+(\.\d+)?(?:[eE][+-]?\d+)?\s*))+$/;
            return re.test(str);
        };
        for (let i = min; i <= max; i += (max - min) / 1000.0) {
            x.push(i);
            if (!checkFormula(formula.replaceAll('x', `${i}`))) {
                valid = false;
                console.log(formula.replaceAll('x', `(${i})`));
                break;
            }
            y.push(eval(formula.replaceAll('x', `(${i})`)));
        }
        if (valid) {
            setData({ x, y });
            setFormulaError(false);
        } else {
            setFormulaError(true);
        }
    }, [formula, min, max]);

    return (
        <div>
            <Plot
                className="plot-target"
                data={[
                    {
                        x: data.x,
                        y: data.y,
                        type: 'scatter',
                        mode: 'lines',
                        line: {
                            width: 1
                        }
                    }
                ]}
                layout={{
                    xaxis: {
                        range: [-5, 5],
                        type: 'linear'
                    },
                    yaxis: {
                        range: [-5, 5],
                        type: 'linear'
                    },
                    height: 400,
                    width: 400
                }}
            />
        </div>
    );
}
