/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

//
//Copyright (C) 2006 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric.solvers;

//TODO: problem: we do not distinguish between ints and reals?
// still needs a lot of work: do not use!


import gov.nasa.jpf.vm.Verify;
import proteus.dl.syntax.Real;

import java.security.acl.NotOwnerException;
import java.util.HashMap;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPExpr;
import com.microsoft.z3.Global;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Params;
import com.microsoft.z3.RatNum;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;

public class ProblemZ3 extends ProblemGeneral {
	Context ctx;
	Solver solver;
	
	protected Model model;

	public ProblemZ3() {
		HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
//        System.out.println(System.getProperty("java.library.path"));

		try{
			Global.setParameter("pp.bv_literals", "false");
			ctx = new Context(cfg);
			solver = ctx.mkSolver();
			
//			Params params = ctx.mkParams();
//			params.add("pp.bv_literals", false);
//			solver.setParameters(params);
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
	    }
	}


	//if min or max are passed in as null objects 
	//it will use minus and plus infinity
	// TODO: to add ranges
	public Object makeIntVar(String name, int min, int max) {
		try{
			//return ctx.mkIntConst(name);
			IntExpr intConst = ctx.mkIntConst(name);
            solver.add(ctx.mkGe(intConst, ctx.mkInt(min)));
            solver.add(ctx.mkLe(intConst, ctx.mkInt(max)));
            return intConst;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}


	// TODO: to add ranges
	public Object makeRealVar(String name, double min, double max) {

		try{
			RealExpr realConst = ctx.mkRealConst(name);
			solver.add(ctx.mkGe(realConst, ctx.mkFPToReal(ctx.mkFP(min, ctx.mkFPSortDouble()))));
			solver.add(ctx.mkLe(realConst, ctx.mkFPToReal(ctx.mkFP(max, ctx.mkFPSortDouble()))));
			return realConst; 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object eq(int value, Object exp){
		try{
			return ctx.mkEq( ctx.mkInt(value), (IntExpr)exp);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object eq(Object exp, int value){
		try{
			return ctx.mkEq( ctx.mkInt(value), (IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	// should we use Expr or ArithExpr?
	public Object eq(Object exp1, Object exp2){
		try{
			return  ctx.mkEq((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	// TODO: should convert double to rational
//	public Object eq(double value, Object exp){
//		try{
//			return  ctx.mkEq(ctx.mkReal(arg0, arg1), (RealExpr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
//
//	    }
//	}

//	public Object eq(Object exp, double value){
//		try{
//			return  ctx.mkEq(ctx.mkReal(arg0, arg1), (RealExpr)exp);;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
//
//	    }
//	}

	public Object neq(int value, Object exp){
		try{
			return ctx.mkNot(ctx.mkEq(ctx.mkInt(value), (IntExpr)exp));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object neq(Object exp, int value){
		try{
			return ctx.mkNot(ctx.mkEq(ctx.mkInt(value), (IntExpr)exp));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object neq(Object exp1, Object exp2){
		try{
			return  ctx.mkNot(ctx.mkEq((Expr)exp1, (Expr)exp2));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object not(Object exp1){
		try{
			return  ctx.mkNot((BoolExpr)exp1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	// TODO: convert doubles to rationals
//	public Object neq(double value, Object exp){
//		try{
//			return  vc.notExpr(vc.eqExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//	public Object neq(Object exp, double value){
//		try{
//			return  vc.notExpr(vc.eqExpr((Expr)exp, vc.ratExpr(Double.toString(value), base)));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}

	public Object leq(int value, Object exp){
		try{
			return  ctx.mkLe(ctx.mkInt(value), (IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object leq(Object exp, int value){
		try{
			return  ctx.mkLe((IntExpr)exp,ctx.mkInt(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object leq(Object exp1, Object exp2){
		try{
			return  ctx.mkLe((ArithExpr)exp1, (ArithExpr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

//	public Object leq(double value, Object exp){
//		try{
//			return  vc.leExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//	public Object leq(Object exp, double value){
//		try{
//			return  vc.leExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
	public Object geq(int value, Object exp){
		try{
			return  ctx.mkGe(ctx.mkInt(value),(IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object geq(Object exp, int value){
		try{
			return  ctx.mkGe((IntExpr)exp,ctx.mkInt(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object geq(Object exp1, Object exp2){
		try{
			return  ctx.mkGe((ArithExpr)exp1,(ArithExpr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

//	public Object geq(double value, Object exp){
//		try{
//			return  vc.geExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//	public Object geq(Object exp, double value){
//		try{
//			return  vc.geExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
	public Object lt(int value, Object exp){
		try{
			return  ctx.mkLt(ctx.mkInt(value),(IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object lt(Object exp, int value){
		try{
			return  ctx.mkLt((IntExpr)exp,ctx.mkInt(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object lt(Object exp1, Object exp2){
		try{
			return  ctx.mkLt((ArithExpr)exp1,(ArithExpr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

//	public Object lt(double value, Object exp){
//		try{
//			return  vc.ltExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//	public Object lt(Object exp, double value){
//		try{
//			return  vc.ltExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//
	public Object gt(int value, Object exp){
		try{
			return  ctx.mkGt(ctx.mkInt(value),(IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object gt(Object exp, int value){
		try{
			return  ctx.mkGt((IntExpr)exp,ctx.mkInt(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

	public Object gt(Object exp1, Object exp2){
		try{
			return  ctx.mkGt((ArithExpr)exp1,(ArithExpr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);

	    }
	}

//	public Object implies(Object exp1, Object exp2){
//		try{
//			return  vc.impliesExpr((Expr)exp1, (Expr)exp2);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}

//	public Object gt(double value, Object exp){
//		try{
//			return  vc.gtExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//	public Object gt(Object exp, double value){
//		try{
//			return  vc.gtExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//
//	    }
//	}
//
//
//
//
	public Object plus(int value, Object exp) {
		try{
			return  ctx.mkAdd(new ArithExpr[] { ctx.mkInt(value), (IntExpr)exp});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object plus(Object exp, int value) {
		try{
			return  ctx.mkAdd(new ArithExpr[] { ctx.mkInt(value), (IntExpr)exp});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object plus(Object exp1, Object exp2) {
		try{
			return  ctx.mkAdd(new ArithExpr[] { (ArithExpr)exp1, (ArithExpr)exp2});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

//	public Object plus(double value, Object exp) {
//		try{
//			return  vc.plusExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}
//
//	public Object plus(Object exp, double value) {
//		try{
//			return  vc.plusExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}

	public Object minus(int value, Object exp) {
		try{
			return  ctx.mkSub(new ArithExpr[] { ctx.mkInt(value), (IntExpr)exp});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object minus(Object exp, int value) {
		try{
			return  ctx.mkSub(new ArithExpr[] {(IntExpr)exp, ctx.mkInt(value)});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object minus(Object exp1, Object exp2) {
		try{
			return  ctx.mkSub(new ArithExpr[] { (ArithExpr)exp1, (ArithExpr)exp2});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

//	public Object minus(double value, Object exp) {
//		try{
//			return  vc.minusExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}
//
//	public Object minus(Object exp, double value) {
//		try{
//			return  vc.minusExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}

	public Object mult(int value, Object exp) {
		try{
			return  ctx.mkMul(new ArithExpr[] {(IntExpr)exp, ctx.mkInt(value)});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object mult(Object exp, int value) {
		try{
			return  ctx.mkMul(new ArithExpr[] {(IntExpr)exp, ctx.mkInt(value)});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object mult(Object exp1, Object exp2) {
		try{
			return  ctx.mkMul(new ArithExpr[] {(ArithExpr)exp1, (ArithExpr)exp2});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}
//	public Object mult(double value, Object exp) {
//		try{
//			return  vc.multExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}
//	public Object mult(Object exp, double value) {
//		try{
//			return  vc.multExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}
//
//	

	public Object div(int value, Object exp) {
		try{
			return  ctx.mkDiv(ctx.mkInt(value), (IntExpr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object div(Object exp, int value) {
		try{
			return  ctx.mkDiv((IntExpr)exp,ctx.mkInt(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}

	public Object div(Object exp1, Object exp2) {
		try{
			return  ctx.mkDiv((ArithExpr)exp1,(ArithExpr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
		}
	}
	
	public Object rem(Object exp, int value) {// added by corina
        try{
                
                return  ctx.mkRem((IntExpr) exp, ctx.mkInt(value));
        } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
        }
	}
	public Object rem(int value, Object exp) {// added by corina
	        try{
	                
	                return  ctx.mkRem(ctx.mkInt(value), (IntExpr) exp);
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
	        }
	}
	public Object rem(Object exp1, Object exp2) {// added by corina
	        try{
	                if(exp2 instanceof Integer)
	                        return  ctx.mkRem((IntExpr)exp1,ctx.mkInt((Integer)exp2));
	                return  ctx.mkRem((IntExpr) exp1, (IntExpr) exp2);
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw new RuntimeException("## Error Z3: Exception caught in Z3 JNI: \n" + e);
	        }
	}


//	public Object div(double value, Object exp) {
//		try{
//			return  vc.divideExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}
//	public Object div(Object exp, double value) {
//		try{
//			return  vc.divideExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
//		}
//	}



	
	

	public int getIntValue(Object dpVar) { 
		try{
//			Model model = null;
			 if (Status.SATISFIABLE == solver.check())
	         {
	             model = solver.getModel();
	             return Integer.parseInt((model.evaluate((IntExpr)dpVar,false)).toString());
	         }
	         else {
	        	 assert false; // should not be reachable
	             return 0;
	         }
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error Z3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	

	private Expr test(){
		Expr e = (Expr)makeIntVar("Z",-10, 10);
		Expr f = (Expr)makeIntVar("f", -10,10);
		Expr plus = (Expr)plus(10,e);
		Expr plus2 = (Expr)plus(1,e);
		Expr eq = (Expr)eq(plus, plus2);
		return eq;
	}

	public Boolean solve() {
        try {
        	/* find model for the constraints above */
//            Model model = null;
        	/*System.out.println("\n------------------start of the problem to be solvered-----------------");
        	System.out.println(solver.toString());
        	System.out.println("------------------end of problem-----------------\n");*/
            if (Status.SATISFIABLE == solver.check())
            {
                model = solver.getModel();
//                System.out.println("\n++++++++++++++++++++++ start of the model ++++++++++++++++++");
//                System.out.println(model);
//                System.out.println("++++++++++++++++++++++ end of the model ++++++++++++++++++\n");
                return true;
            } else
          
                return false;
        	
   
        }catch(Exception e){
        	e.printStackTrace();
        	throw new RuntimeException("## Error Z3: " + e);
        }
	}

	public void post(Object constraint) {
		try{
			solver.add((BoolExpr)constraint);
		} catch (Exception e) {
			e.printStackTrace();
        	throw new RuntimeException("## Error Z3 \n" + e);
	    }
	}


	
	// need to implement all of these
	@Override
	public Object eq(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkEq(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}		
	}


	@Override
	public Object eq(Object exp, double value) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkEq((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object neq(double value, Object exp) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkNot(ctx.mkEq(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp));
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object neq(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkNot(ctx.mkEq((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble()))));
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object leq(double value, Object exp) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkLe(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object leq(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkLe((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}




	@Override
	public Object geq(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkGe(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object geq(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkGe((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}





	@Override
	public Object lt(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkLt(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object lt(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkLt((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));
		}catch(Z3Exception e){
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}





	@Override
	public Object gt(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkGt(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object gt(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkGt((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n");
		}		
	}



	@Override
	public Object minus(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkSub(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object minus(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkSub((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}



	@Override
	public Object mult(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkMul(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object mult(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkMul((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}

	@Override
	public Object div(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkDiv(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object div(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkDiv((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object and(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVAND(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object and(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVAND((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object and(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVAND((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object or(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVAND(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object or(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVOR((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}		
	}


	@Override
	public Object or(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVOR((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object xor(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVXOR(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object xor(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVXOR((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object xor(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVXOR((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}

	

	@Override
	public Object shiftL(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVSHL(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftL(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVSHL((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftL(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVSHL((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftR(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVASHR(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftR(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVASHR((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftR(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVASHR((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftUR(int value, Object exp) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVLSHR(ctx.mkBV(value, 32), (BitVecExpr)exp);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftUR(Object exp, int value) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVLSHR((BitVecExpr)exp, ctx.mkBV(value, 32));			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object shiftUR(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try{			
			return ctx.mkBVLSHR((BitVecExpr)exp1, (BitVecExpr)exp2);			
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}


	@Override
	public Object mixed(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		if (exp1 instanceof RealExpr && exp2 instanceof IntExpr) {
			return ctx.mkEq((RealExpr)exp1, ctx.mkInt2Real((IntExpr)exp2));
		}
		else
			throw new RuntimeException("## Error Z3: unsupported mixed case");
	}


	@Override
	public double getRealValueInf(Object dpvar) {
		// TODO Auto-generated method stub
		throw new RuntimeException("## Error Z3 \n");//return 0;
	
	}


	@Override
	public double getRealValueSup(Object dpVar) {
		// TODO Auto-generated method stub
		throw new RuntimeException("## Error Z3 \n");//return 0;
	}


	@Override
	public double getRealValue(Object dpVar) {
		// TODO Auto-generated method stub
		try{			
			RatNum expr = (RatNum)model.getConstInterp((Expr)dpVar);
			double num = Double.parseDouble(expr.getNumerator().toString());
//			System.out.println("The numerator is : " + num);
			double den = Double.parseDouble(expr.getDenominator().toString());
//			System.out.println("The denominator is : " + den);
//			int den = expr.getDenominator().getInt();
			return num/den;
		}catch (Z3Exception e){
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);//return 0;
		}		
	}


	@Override
	public void postLogicalOR(Object[] constraint) {
		// TODO Auto-generated method stub
		post(ctx.mkOr(ctx.mkFalse()));
		for (int i = 0; i < constraint.length; i++){
			post(ctx.mkOr((BoolExpr)constraint[i]));
		}
		//throw new RuntimeException("## Error Z3 \n");	
	}


	@Override
	public Object plus(double value, Object exp) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkAdd(ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())), (RealExpr)exp);
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}	
	}


	@Override
	public Object plus(Object exp, double value) {
		// TODO Auto-generated method stub
		try{
			return ctx.mkAdd((RealExpr)exp, ctx.mkFPToReal(ctx.mkFP(value, ctx.mkFPSortDouble())));
		}catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}

	@Override
	public Object power(double exp1, Object exp2) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkPower(ctx.mkFPToReal(ctx.mkFP(exp1, ctx.mkFPSortDouble())), (RealExpr)exp2);
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}		
	}	
	
	@Override
	public Object power(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkPower((RealExpr)exp1, (RealExpr)exp2);
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}
	
	@Override
	public Object power(Object exp1, double exp2) {
		// TODO Auto-generated method stub
		try {
			return ctx.mkPower((RealExpr)exp1, ctx.mkFPToReal(ctx.mkFP(exp2, ctx.mkFPSortDouble())));
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}	
	
	@Override
	public Object sqrt(Object exp) {
		// TODO Auto-generated method stub
		try {
			FPExpr fpExpr = ctx.mkFPSqrt(ctx.mkFPRoundNearestTiesToAway(), 
					ctx.mkFPToFP(ctx.mkFPRoundNearestTiesToAway(), (RealExpr)exp, ctx.mkFPSort64())); 
			return ctx.mkFPToReal(fpExpr);
		} catch (Z3Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException("## Error Z3 \n" + e);
		}
	}

}