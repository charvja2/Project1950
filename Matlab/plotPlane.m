
pointA = [0 0 0];
pointB = [0 1 0];
pointC = [0 0 1];
points=[pointA' pointB' pointC']; % using the data given in the question
fill3(points(1,:),points(2,:),points(3,:),'r')
grid on
alpha(0.3)

x=-10:.1:10;
[X,Y] = meshgrid(x);
a=2; b=-3; c=10; d=-1;
Z=(d- a * X - b * Y)/c;
surf(X,Y,Z)
shading flat
xlabel('x'); ylabel('y'); zlabel('z')