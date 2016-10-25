
amp = 1;
range = 3;
desired = 2;
x1 = 0:0.01:desired;
x2 = desired:0.01:range;

y1 = -((x1-desired).^2);
 y1 = y1*(amp/abs(min(y1)));

y2 = ((x2-desired).^2);
 y2 = y2*(amp/abs(max(y2)));


figure;
plot(x1,y1);
hold on;
 plot(x2,y2);
hold off;
grid on;

