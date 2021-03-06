/*
 * Copyright (C) 2021 MaxSav Team
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.maxsavteam.calculator.utils;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.maxsavteam.calculator.exceptions.CalculatingException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {
    public static final BigDecimal E = new BigDecimal("2.7182818284590452354");
    public static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
    public static final BigDecimal FI = new BigDecimal("1.618");
    private static final BigDecimal factorialLimit = new BigDecimal("100000");

    public static final int roundScale = 8;
    private static final int mHighRoundScale = 20;

    public static BigDecimal exp(BigDecimal x) {
        return BigDecimalMath.exp(x, new MathContext(mHighRoundScale));
    }

    public static BigDecimal ln(BigDecimal x) {
        if (x.signum() < 0)
            throw new CalculatingException(CalculatingException.NEGATIVE_PARAMETER_OF_LOG);
        return BigDecimalMath.log(x, new MathContext(mHighRoundScale));
    }

    public static BigDecimal log(BigDecimal x) {
        if (x.signum() < 0)
            throw new CalculatingException(CalculatingException.NEGATIVE_PARAMETER_OF_LOG);
        return BigDecimalMath.log10(x, new MathContext(mHighRoundScale));
    }

    public static BigDecimal logWithBase(BigDecimal x, BigDecimal base) {
        if (x.signum() < 0)
            throw new CalculatingException(CalculatingException.NEGATIVE_PARAMETER_OF_LOG);
        if (base.compareTo(BigDecimal.TEN) == 0)
            return BigDecimalMath.log10(x, new MathContext(roundScale));
        if (base.compareTo(BigDecimal.valueOf(2)) == 0)
            return BigDecimalMath.log2(x, new MathContext(roundScale));

        BigDecimal logX = BigDecimalMath.log2(x, new MathContext(mHighRoundScale)),
                logB = BigDecimalMath.log2(base, new MathContext(mHighRoundScale));
        return logX.divide(logB, 8, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal abs(BigDecimal x) {
        if (x.signum() < 0) {
            return x.multiply(new BigDecimal("-1"));
        } else {
            return x;
        }
    }

    public static BigDecimal tan(BigDecimal x) {
        if (x.compareTo(BigDecimal.valueOf(90)) == 0)
            throw new CalculatingException(CalculatingException.TAN_OF_90);
        return BigDecimalMath.tan(toRadians(x), new MathContext(roundScale));
    }

    public static BigDecimal sin(BigDecimal x) {
        return BigDecimalMath.sin(toRadians(x), new MathContext(6));
    }

    public static BigDecimal cos(BigDecimal x) {
        return BigDecimalMath.cos(toRadians(x), new MathContext(6));
    }

    public static BigDecimal fact(BigDecimal a, int step) {
        if (a.compareTo(factorialLimit) > 0)
            throw new CalculatingException(CalculatingException.FACTORIAL_LIMIT_EXCEEDED);
        if (a.signum() == 0)
            return BigDecimal.ONE;
        if (step == 1 && a.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0)
            return factTree(1L, a.longValue());
        BigDecimal x = a;
        BigDecimal ans = BigDecimal.ONE;
        BigDecimal bStep = BigDecimal.valueOf(step);
        for (; 0 <= x.compareTo(BigDecimal.ONE); x = x.subtract(bStep)) {
            ans = ans.multiply(x);
        }
        return ans;
    }

    private static BigDecimal factTree(long l, long r) {
        if (l > r)
            return BigDecimal.ONE;
        if (l == r)
            return BigDecimal.valueOf(l);
        if (r - l == 1)
            return BigDecimal.valueOf(l * r);
        long m = l + (r - l) / 2;
        return factTree(l, m).multiply(factTree(m + 1, r));
    }

    public static BigDecimal floor(BigDecimal x) {
        String s = x.toPlainString();
        int pos = s.indexOf(".");
        if (pos == -1 || pos == s.length() - 1) {
            return x;
        } else {
            return new BigDecimal(s.substring(0, pos));
        }
    }

    public static BigDecimal ceil(BigDecimal x) {
        String s = x.toPlainString();
        int pos = s.indexOf(".");
        if (pos == -1 || pos == s.length() - 1) {
            return x;
        } else {
            String afterDot = s.substring(pos + 1);
            if (CalculatorUtils.removeZeros(afterDot).equals("0"))
                return new BigDecimal(s.substring(0, pos)); // delete part after dot

            BigDecimal b = abs(new BigDecimal(s.substring(0, pos)));
            b = b.add(BigDecimal.ONE);
            if (s.charAt(0) == '-')
                b = b.multiply(BigDecimal.valueOf(-1));
            return b;
        }
    }

    public static BigDecimal round(BigDecimal x) {
        String s = x.toPlainString();
        int pos = s.indexOf(".");
        if (pos == -1 || pos == s.length() - 1) {
            return x;
        } else {
            String newString = s.substring(0, pos);
            char next = s.charAt(pos + 1);
            if (next >= '0' && next < '5') {
                return new BigDecimal(newString);
            } else {
                BigDecimal b = abs(new BigDecimal(newString));
                b = b.add(BigDecimal.ONE);
                if (newString.charAt(0) == '-')
                    b = b.multiply(BigDecimal.valueOf(-1));
                return b;
            }
        }
    }

    public static BigDecimal rootWithBase(BigDecimal a, BigDecimal n) {
        if(a.signum() == 0)
            return BigDecimal.ZERO;
        if (n.remainder(BigDecimal.valueOf(2)).signum() == 0 && a.signum() < 0)
            throw new CalculatingException(CalculatingException.ROOT_OF_EVEN_DEGREE_OF_NEGATIVE_NUMBER);
        BigDecimal log = ln(a);
        BigDecimal dLog = log.divide(n, mHighRoundScale, RoundingMode.HALF_EVEN);
        return exp(dLog);
    }

    public static BigDecimal powWithExp(BigDecimal a, BigDecimal n) {
        BigDecimal ln = ln(a);
        BigDecimal multiplying = n.multiply(ln);
        return exp(multiplying);
    }

    public static BigDecimal pow(BigDecimal a, BigDecimal n) {
        if (a.signum() == 0) {
            if (n.signum() < 0)
                throw new CalculatingException(CalculatingException.NAN);
            else if (n.signum() == 0)
                throw new CalculatingException(CalculatingException.UNDEFINED);
            else
                return BigDecimal.ZERO;
        }
        if (n.signum() < 0) {
            BigDecimal result = pow(a, n.multiply(BigDecimal.valueOf(-1)));
            String strRes = BigDecimal.ONE.divide(result, 8, RoundingMode.HALF_EVEN).toPlainString();
            return new BigDecimal(CalculatorUtils.removeZeros(strRes));
        }
        if (Fraction.isFraction(n)) {
            BigDecimal scaledN = n.setScale(3, RoundingMode.HALF_DOWN);
            Fraction fraction = new Fraction(scaledN);
            return MathUtils.rootWithBase(sysPow(a, fraction.getNumerator()), fraction.getDenominator());
        }
        return sysPow(a, n);
    }

    private static BigDecimal sysPow(BigDecimal a, BigDecimal n) {
        if (n.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        if (n.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ONE) == 0) {
            return sysPow(a, n.subtract(BigDecimal.ONE)).multiply(a);
        } else {
            BigDecimal b = sysPow(a, n.divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_EVEN));
            return b.multiply(b);
        }
    }

    public static BigDecimal toRadians(BigDecimal decimal) {
        return decimal.multiply(PI).divide(BigDecimal.valueOf(180), 8, RoundingMode.HALF_EVEN);
    }
}
