/*
 * Copyright 2010, 2011 Institut Pasteur.
 * 
 * This file is part of NHerve Main Toolbox, which is an ICY plugin.
 * 
 * NHerve Main Toolbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NHerve Main Toolbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NHerve Main Toolbox. If not, see <http://www.gnu.org/licenses/>.
 */
package plugins.nherve.toolbox.image.feature.region;

import java.awt.Point;
import java.util.Vector;

/**
 * The Class EllipseFit.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class EllipseFit { //int np, Point *points, double **XY
	
	/** The np. */
 private int np ;// points.size();           // number of points
	
	/** The points. */
	private Vector<Point> points;//=new Vector<Point>();

	/** The D. */
	private double D[][] = new double[np+1][7];
	
	/** The S. */
	private double S[][] = new double[7][7];
	
	/** The Const. */
	private double Const[][]  = new double[7][7];
	
	/** The temp. */
	private double temp[][] = new double [7][7];
	
	/** The L. */
	private double L[][] = new double [7][7]; 
	
	/** The C. */
	private double C[][] = new double [7][7]; 

	/** The inv l. */
	private double invL[][] = new double [7][7];
	
	/** The d. */
	private double d[] = new double [7];
	
	/** The V. */
	private double V[][] = new double [7][7]; 
	
	/** The sol. */
	private double sol[][] = new double [7][7];
	
	/** The ty. */
	private double tx,ty;
	
	/** The nrot. */
	private int nrot=0;
	
	/** The npts. */
	private int npts=50;

	/** The XY. */
	private double XY[][] = new double[3][npts+1];
	
	/** The pvec. */
	private double pvec[] = new double[7];

	/**
	 * Instantiates a new ellipse fit.
	 * 
	 * @param npa
	 *            the npa
	 * @param pointsa
	 *            the pointsa
	 * @param XYa
	 *            the x ya
	 */
	public EllipseFit(int npa, Vector <Point> pointsa, double[][] XYa )
	{
		this.np=npa;
		this.points = pointsa;
		Point pt=new Point();
		for (int i=0; i < np; i++)
		{ 
		points.add(pt=pointsa.elementAt(i));
	//	System.out.println(" ptx : "+pt.x +" pty : "+pt.y);
		}
		//XY = XYa;
		computeEllipse();
		for (int i=1; i<=npts; i++) {
			 XYa[1][i] = XY[1][i];
			 XYa[2][i] = XY[2][i];
		}
		
		// TODO return an object !!!!!
		//return XY;
	}
	
	/**
	 * Compute ellipse.
	 */
	public void computeEllipse(){
	// Case FPF

	Const[1][3]=-2;
	Const[2][2]=1;
	Const[3][1]=-2;	

	
	if (np<6)
		return;

	//System.out.println(" EllipseFit : "+np);
	// Now first fill design matrix
	D = new double[np+1][7];
	for (int i=1; i <= np; i++)
	{ 
	tx = (points.elementAt(i-1)).x;
  	ty = (points.elementAt(i-1)).y;
  //	System.out.println(" tx : "+tx +" ty : "+ty);
  
		D[i][1] = tx*tx;
		D[i][2] = tx*ty;
		D[i][3] = ty*ty;
		D[i][4] = tx;
		D[i][5] = ty;
		D[i][6] = 1.0;
		
	}
/*	System.out.println(" matrice D");
	for (int  i=1; i<=np; i++) {
		System.out.print(D[i][1] +" ");
		System.out.print(D[i][2] +" ");
		System.out.print(D[i][3] +" ");
		System.out.print(D[i][4] +" ");
		System.out.print(D[i][5] +" ");
		System.out.println(D[i][6] +" ");
	}*/
      //pm(Const,"Constraint");
      // Now compute scatter matrix  S
    A_TperB(D,D,S,np,6,np,6);
    choldc(S,6,L);    
    //System.out.println("Inverse L");
    inverse(L,invL,6);
  //  pm(invL,"inverse");
	
    AperB_T(Const,invL,temp,6,6,6,6);
    AperB(invL,temp,C,6,6,6,6);
  //  pm(C,"The C matrix");
    jacobi(C,6,d,V,nrot);
    A_TperB(invL,V,sol,6,6,6,6);
    
    // Now normalize them 
    for (int j=1;j<=6;j++)  /* Scan columns */
    {
    	double mod = 0.0;
    	for (int i=1;i<=6;i++)
    		mod += sol[i][j]*sol[i][j];
    	for (int i=1;i<=6;i++)
    		sol[i][j] /=  Math.sqrt(mod); 
    }

    double zero=10e-20;
  //  double minev=10e+20;
    int  solind=0;
    for (int i=1; i<=6; i++)
    	if (d[i]<0 && Math.abs(d[i])>zero)	
    	  solind = i;
        
        // Now fetch the right solution
        for (int j=1;j<=6;j++)
          pvec[j] = sol[j][solind];

        
        // ...and plot it	
        draw_conic(pvec,npts,XY);	
        
        // TODO :        /* Free Memory */
    
	}



	  /**
	 * ROTATE.
	 * 
	 * @param a
	 *            the a
	 * @param i
	 *            the i
	 * @param j
	 *            the j
	 * @param k
	 *            the k
	 * @param l
	 *            the l
	 * @param tau
	 *            the tau
	 * @param s
	 *            the s
	 */
  	private void ROTATE(double a[][], int i, int j, int k, int l, double tau, double s) 
	    {
	      double g,h;
	      g=a[i][j];h=a[k][l];a[i][j]=g-s*(h+g*tau);
	      a[k][l]=h+s*(g-h*tau);
	    }
	  
	/**
	 * A_ tper b.
	 * 
	 * @param _A
	 *            the _ a
	 * @param _B
	 *            the _ b
	 * @param _res
	 *            the _res
	 * @param _righA
	 *            the _righ a
	 * @param _colA
	 *            the _col a
	 * @param _righB
	 *            the _righ b
	 * @param _colB
	 *            the _col b
	 */
	private void A_TperB(double _A[][], double  _B[][], double _res[][],
			int _righA, int _colA, int _righB, int _colB) {
		int p,q,l;                                      
		for (p=1;p<=_colA;p++)                        
			for (q=1;q<=_colB;q++)                        
			{ _res[p][q]=0.0;                            
			for (l=1;l<=_righA;l++)                    
				_res[p][q]=_res[p][q]+_A[l][p]*_B[l][q];  
			}                                            
	}

    
	  /**
	 * Aper b.
	 * 
	 * @param _A
	 *            the _ a
	 * @param _B
	 *            the _ b
	 * @param _res
	 *            the _res
	 * @param _righA
	 *            the _righ a
	 * @param _colA
	 *            the _col a
	 * @param _righB
	 *            the _righ b
	 * @param _colB
	 *            the _col b
	 */
  	private void AperB(double _A[][], double _B[][], double _res[][], 
			     int _righA, int _colA, int _righB, int _colB) {
	    int p,q,l;                                      
	    for (p=1;p<=_righA;p++)                        
	      for (q=1;q<=_colB;q++)                        
		{ _res[p][q]=0.0;                            
		for (l=1;l<=_colA;l++)                     
		  _res[p][q]=_res[p][q]+_A[p][l]*_B[l][q];  
		}                                            
	  }     
	  
	/**
	 * Aper b_ t.
	 * 
	 * @param _A
	 *            the _ a
	 * @param _B
	 *            the _ b
	 * @param _res
	 *            the _res
	 * @param _righA
	 *            the _righ a
	 * @param _colA
	 *            the _col a
	 * @param _righB
	 *            the _righ b
	 * @param _colB
	 *            the _col b
	 */
	private void AperB_T(double _A[][], double _B[][], double _res[][],
			int _righA, int _colA, int _righB, int _colB) {
		int p,q,l;                                      
		for (p=1;p<=_colA;p++)                         
			for (q=1;q<=_colB;q++)                        
			{ _res[p][q]=0.0;                            
			for (l=1;l<=_righA;l++)                    
				_res[p][q]=_res[p][q]+_A[p][l]*_B[q][l];  
			}                                            
	}


	  //  Perform the Cholesky decomposition    
	  // Return the lower triangular L  such that L*L'=A  
	  /**
	 * Choldc.
	 * 
	 * @param a
	 *            the a
	 * @param n
	 *            the n
	 * @param l
	 *            the l
	 */
  	private void choldc(double a[][], int n, double l[][])
	    {
	      int i,j,k;
	      double sum;
	      double p[] = new double[n+1];
		
	      for (i=1; i<=n; i++)  {
		for (j=i; j<=n; j++)  {
		  for (sum=a[i][j],k=i-1;k>=1;k--) sum -= a[i][k]*a[j][k];
		  if (i == j) {
		    if (sum<=0.0)  
		      // printf("\nA is not poitive definite!");
		      {}
		    else 
		      p[i]=Math.sqrt(sum); }
		  else 
		    {
		      a[j][i]=sum/p[i];
		    }
		}
	      }       
	      for (i=1; i<=n; i++)  
		for (j=i; j<=n; j++)  
		  if (i==j)
		    l[i][i] = p[i];
		  else
		    {
		      l[j][i]=a[j][i];  
		      l[i][j]=0.0;
		    }    
	    }

	  /**
	 * Draw_conic.
	 * 
	 * @param pvec
	 *            the pvec
	 * @param nptsk
	 *            the nptsk
	 * @param pts
	 *            the pts
	 */
  	public void draw_conic(double pvec[], int nptsk, double pts[][])  {
		    int npts=nptsk/2;	
		    double u[][] = new double[3][npts+1];
		    double Aiu[][] = new double[3][npts+1];
		    double L[][] = new double[3][npts+1];
		    double B[][] = new double[3][npts+1];
		    double Xpos[][] = new double[3][npts+1];
		    double Xneg[][] = new double[3][npts+1];
		    double ss1[][] = new double[3][npts+1];
		    double ss2[][] = new double[3][npts+1];
		    double lambda[] = new double[npts+1];
		    double uAiu[][] = new double[3][npts+1];
		    double A[][] = new double[3][3];
		    double Ai[][] = new double[3][3];
		    double Aib[][] = new double[3][2];
		    double b[][] = new double[3][2];
		    double r1[][] = new double[2][2];
		    double Ao, Ax, Ay, Axx, Ayy, Axy;
		             
		    double pi = 3.14781;      
		    double theta;
		    int i;
		    int j;
		    double kk;
		      
		    Ao = pvec[6];
		    Ax = pvec[4];
		    Ay = pvec[5];
		    Axx = pvec[1];
		    Ayy = pvec[3];
		    Axy = pvec[2];

		    A[1][1] = Axx;    A[1][2] = Axy/2;
		    A[2][1] = Axy/2;  A[2][2] = Ayy;
		    b[1][1] = Ax; b[2][1] = Ay;  

		    // Generate normals linspace
		    for (i=1, theta=0.0; i<=npts; i++, theta+=(pi/npts)) {
		      u[1][i] = Math.cos(theta);
		      u[2][i] = Math.sin(theta); }

		    //System.out.println("Inverse A");
		    inverse(A,Ai,2);
		 
		    AperB(Ai,b,Aib,2,2,2,1);
		    A_TperB(b,Aib,r1,2,1,2,1);      
		    r1[1][1] = r1[1][1] - 4*Ao;
/*		    System.out.println("------------Aib matrix--------------");
		    System.out.println(" " + Aib[1][1] + " " + Aib[2][1]  );
		    System.out.println("----------- End Aib --------------");
*/
		    AperB(Ai,u,Aiu,2,2,2,npts);
		    
	/*	    System.out.println("------------Aiu matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + Aiu[1][i] + " " + Aiu[2][i]  );
	*/	      
		    for (i=1; i<=2; i++)
		      for (j=1; j<=npts; j++)
			uAiu[i][j] = u[i][j] * Aiu[i][j];
		    
		/*    System.out.println("------------uAiu matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + uAiu[1][i] + " " + uAiu[2][i]  );
*/
		    for (j=1; j<=npts; j++) {
		      if ( (kk=(r1[1][1] / (uAiu[1][j]+uAiu[2][j]))) >= 0.0)
			lambda[j] = Math.sqrt(kk);
		      else
			lambda[j] = -1.0; }
		    
	/*	    System.out.println("------------lambda matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + lambda[i] );
*/
		    // Builds up B and L
		    for (j=1; j<=npts; j++)
		      L[1][j] = L[2][j] = lambda[j];      
		    for (j=1; j<=npts; j++) {
		      B[1][j] = b[1][1];
		      B[2][j] = b[2][1]; }
		      
	/*	    System.out.println("------------ b matrix--------------");
		    System.out.println(" " + B[1][1] + " " + B[2][1]  );
*/
		    for (j=1; j<=npts; j++) {
		      ss1[1][j] = 0.5 * (  L[1][j] * u[1][j] - B[1][j]);
		      ss1[2][j] = 0.5 * (  L[2][j] * u[2][j] - B[2][j]);
		      ss2[1][j] = 0.5 * ( -L[1][j] * u[1][j] - B[1][j]);
		      ss2[2][j] = 0.5 * ( -L[2][j] * u[2][j] - B[2][j]); }
/*
		    System.out.println("------------ss1 matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + ss1[1][i] + " " + ss1[2][i]  );

		    System.out.println("------------ss2 matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + ss2[1][i] + " " + ss2[2][i]  );
*/
		    AperB(Ai,ss1,Xpos,2,2,2,npts);
		    AperB(Ai,ss2,Xneg,2,2,2,npts);
		    
		/*    System.out.println("------------ Xpos matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + Xpos[1][i] + " " + Xpos[2][i]  );
*/
		    for (j=1; j<=npts; j++) {
		      if (lambda[j]==-1.0) {
			pts[1][j] = -1.0;
			pts[2][j] = -1.0;
			pts[1][j+npts] = -1.0;
			pts[2][j+npts] = -1.0;
		      }
		      else {
			pts[1][j] = Xpos[1][j];
			pts[2][j] = Xpos[2][j];
			pts[1][j+npts] = Xneg[1][j];
			pts[2][j+npts] = Xneg[2][j];
		      }	  	                 
		    }
		 /*   System.out.println("------------ points matrix--------------");
		    for (i=1; i<=npts; i++)
		      System.out.println(" " + pts[1][i] + " " + pts[2][i]  );
*/
		}

/* Inverse */
	  /**
 * Inverse.
 * 
 * @param TB
 *            the tB
 * @param InvB
 *            the inv b
 * @param N
 *            the n
 * @return the int
 */
int inverse(double TB[][], double InvB[][], int N) {  
		    int k,i,j,p,q;
		    double mult;
		    double D,temp;
		    double maxpivot;
		    int npivot;
		    double B[][] = new double [N+1][N+2];
		    double A[][] = new double [N+1][2*N+2];
		//    double C[][] = new double [N+1][N+1];
		    double eps = 10e-20;
		      
		  /*  System.out.println(" matrice");
		    for ( j=1; j<=N; j++)
		    for ( i=1; i<=N; i++) {
			System.out.println(TB[j][i] +" ");

		    }*/
		   
		    for(k=1;k<=N;k++)
		      for(j=1;j<=N;j++)
			B[k][j]=TB[k][j];
		      
		    for (k=1;k<=N;k++)
		      {
			for (j=1;j<=N+1;j++)
			  A[k][j]=B[k][j];
			for (j=N+2;j<=2*N+1;j++)
			  A[k][j]=(float)0;
			A[k][k-1+N+2]=(float)1;
		      }
		    for (k=1;k<=N;k++)
		      {
			maxpivot=Math.abs((double)A[k][k]);
			npivot=k;
			for (i=k;i<=N;i++)
			  if (maxpivot<Math.abs((double)A[i][k]))
			    {
			      maxpivot=Math.abs((double)A[i][k]);
			      npivot=i;
			    }
			if (maxpivot>=eps)
			  {      if (npivot!=k)
			    for (j=k;j<=2*N+1;j++)
			      {
				temp=A[npivot][j];
				A[npivot][j]=A[k][j];
				A[k][j]=temp;
			      } ;
			  D=A[k][k];
			  for (j=2*N+1;j>=k;j--)
			    A[k][j]=A[k][j]/D;
			  for (i=1;i<=N;i++)
			    {
			      if (i!=k)
				{
				  mult=A[i][k];
				  for (j=2*N+1;j>=k;j--)
				    A[i][j]=A[i][j]-mult*A[k][j] ;
				}
			    }
			  }
			else
			  {  // printf("\n The matrix may be singular !!") ;
			    return(-1);
			  };
		      }
		    /**   Copia il risultato nella matrice InvB  ***/
		    for (k=1,p=1;k<=N;k++,p++)
		      for (j=N+2,q=1;j<=2*N+1;j++,q++)
			InvB[p][q]=A[k][j];


	/*	    System.out.println("Inverse ");
		    for ( j=1; j<=N; j++)
		      for ( i=1; i<=N; i++) {
			System.out.println(InvB[j][i] +" ");
		    }
		   */

		    return(0);
		  }            /*  End of INVERSE   */
		    

	  /**
	 * Jacobi.
	 * 
	 * @param a
	 *            the a
	 * @param n
	 *            the n
	 * @param d
	 *            the d
	 * @param v
	 *            the v
	 * @param nrot
	 *            the nrot
	 */
  	private void jacobi(double a[][], int n, double d[] , double v[][], int nrot)      
	    {
	      int j,iq,ip,i;
	      double tresh,theta,tau,t,sm,s,h,g,c;

	      double b[] = new double[n+1];
	      double z[] = new double[n+1];
		
	      for (ip=1;ip<=n;ip++) {
		for (iq=1;iq<=n;iq++) v[ip][iq]=0.0;
		v[ip][ip]=1.0;
	      }
	      for (ip=1;ip<=n;ip++) {
		b[ip]=d[ip]=a[ip][ip];
		z[ip]=0.0;
	      }
	      nrot=0;
	      for (i=1;i<=50;i++) {
		sm=0.0;
		for (ip=1;ip<=n-1;ip++) {
		  for (iq=ip+1;iq<=n;iq++)
		    sm += Math.abs(a[ip][iq]);
		}
		if (sm == 0.0) {
		  /*    free_vector(z,1,n);
			free_vector(b,1,n);  */
		  return;
		}
		if (i < 4)
		  tresh=0.2*sm/(n*n);
		else
		  tresh=0.0;
		for (ip=1;ip<=n-1;ip++) {
		  for (iq=ip+1;iq<=n;iq++) {
		    g=100.0*Math.abs(a[ip][iq]);
		    if (i > 4 && Math.abs(d[ip])+g == Math.abs(d[ip])
			&& Math.abs(d[iq])+g == Math.abs(d[iq]))
		      a[ip][iq]=0.0;
		    else if (Math.abs(a[ip][iq]) > tresh) {
		      h=d[iq]-d[ip];
		      if (Math.abs(h)+g == Math.abs(h))
			t=(a[ip][iq])/h;
		      else {
			theta=0.5*h/(a[ip][iq]);
			//System.out.println(" theta:"+theta);
			t=1.0/(Math.abs(theta)+Math.sqrt(1.0+theta*theta));
			if (theta < 0.0) t = -t;
		      }
		      c=1.0/Math.sqrt(1+t*t);
		      s=t*c;
		      tau=s/(1.0+c);
		      h=t*a[ip][iq];
		      z[ip] -= h;
		      z[iq] += h;
		      d[ip] -= h;
		      d[iq] += h;
		      a[ip][iq]=0.0;
		      for (j=1;j<=ip-1;j++) {
			ROTATE(a,j,ip,j,iq,tau,s);
		      }
		      for (j=ip+1;j<=iq-1;j++) {
			ROTATE(a,ip,j,j,iq,tau,s);
		      }
		      for (j=iq+1;j<=n;j++) {
			ROTATE(a,ip,j,iq,j,tau,s);
		      }
		      for (j=1;j<=n;j++) {
			ROTATE(v,j,ip,j,iq,tau,s);
		      }
		      ++nrot;
		    }
		  }
		}
		
		for (ip=1;ip<=n;ip++) {
		  b[ip] += z[ip];
		  d[ip]=b[ip];
		  z[ip]=0.0;
		}
	      }
	      
	      //printf("Too many iterations in routine JACOBI");
	    }
	    

	  
}
