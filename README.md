# seam-carver
Seam carving is an algorithm for "content-aware image resizing" developed by Shai Avidan and Ariel Shamir. 

Steps
-----
1. Analyze the picture to create an energy map   
This program uses the *dual-gradient* energy function: energy(x, y) = Rx²+Gx²+Bx² + Ry²+Gy²+By², where Rx is the absolute value of the difference between the red components of (x-1, y) and (x+1, y). Gx, Bx, Ry, Gy, and By are similarly defined.  

2. Calculate the path from top to bottom (or left to right)  
This part uses dynamic programming to create a accumulated cost matrix. It then backtracks through the matrix to calculate the lowest energy path.  

3. Remove path to decrease width (or height) by 1  
Removes each pixel in the path from the image, shrinking the width or height by 1 pixel.  

LINKS:   
http://www.faculty.idc.ac.il/arik/SCWeb/imret/index.html   
https://en.wikipedia.org/wiki/Seam_carving   
https://stacks.stanford.edu/file/druid:my512gb2187/Zargham_Nassirpour_Content_aware_image_resizing.pdf   
