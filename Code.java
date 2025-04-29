ArrayList<Ball> balls;
float gravity = 0.5;
int baseSize = 30;
int numBalls = 20;

void setup() {
  size(800, 600);
  balls = new ArrayList<Ball>();
  createBalls();
}

void draw() {
  background(255);
  fill(0);
  text("Gravity: " + nf(gravity, 1, 2) + " | Ball Size: " + baseSize + " | Balls: " + balls.size(), 10, 20);

  // Update and display all balls
  for (Ball b : balls) {
    b.applyGravity();
    b.update();
    b.checkEdges();
  }

  // Handle collisions
  for (int i = 0; i < balls.size(); i++) {
    for (int j = i+1; j < balls.size(); j++) {
      balls.get(i).checkCollision(balls.get(j));
    }
  }

  for (Ball b : balls) {
    b.display();
  }
}

void createBalls() {
  balls.clear();
  for (int i = 0; i < numBalls; i++) {
    float sizeFactor = random(0.9, 1.1);
    float r = baseSize * sizeFactor;
    float x = random(r, width - r);
    float y = random(r, height/2);
    balls.add(new Ball(x, y, r));
  }
}

void keyPressed() {
  if (keyCode == UP) gravity += 0.1;
  if (keyCode == DOWN) gravity = max(0, gravity - 0.1);
  if (keyCode == RIGHT) baseSize += 2;
  if (keyCode == LEFT) baseSize = max(5, baseSize - 2);
  if (key == '+') numBalls += 5;
  if (key == '-') numBalls = max(1, numBalls - 5);

  createBalls();  // refresh with new settings
}

class Ball {
  float x, y;
  float vx, vy;
  float r, mass;

  Ball(float x, float y, float r) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.mass = r * r;  // rough approximation: area ∝ mass
    this.vx = random(-2, 2);
    this.vy = random(-2, 2);
  }

  void applyGravity() {
    vy += gravity;
  }

  void update() {
    x += vx;
    y += vy;
  }

  void checkEdges() {
    if (x - r < 0) {
      x = r;
      vx *= -0.8;
    }
    if (x + r > width) {
      x = width - r;
      vx *= -0.8;
    }
    if (y + r > height) {
      y = height - r;
      vy *= -0.7;
    }
  }

  void checkCollision(Ball other) {
    float dx = other.x - x;
    float dy = other.y - y;
    float dist = dist(x, y, other.x, other.y);
    float minDist = r + other.r;

    if (dist < minDist && dist > 0) {
      // Push balls apart
      float overlap = 0.5 * (minDist - dist);
      float angle = atan2(dy, dx);
      float sx = cos(angle) * overlap;
      float sy = sin(angle) * overlap;

      x -= sx;
      y -= sy;
      other.x += sx;
      other.y += sy;

      // Elastic collision response
      float nx = dx / dist;
      float ny = dy / dist;
      float tx = -ny;
      float ty = nx;

      // Dot products
      float dpTan1 = vx * tx + vy * ty;
      float dpTan2 = other.vx * tx + other.vy * ty;

      float dpNorm1 = vx * nx + vy * ny;
      float dpNorm2 = other.vx * nx + other.vy * ny;

      // Conservation of momentum (1D along normal)
      float m1 = (dpNorm1 * (mass - other.mass) + 2 * other.mass * dpNorm2) / (mass + other.mass);
      float m2 = (dpNorm2 * (other.mass - mass) + 2 * mass * dpNorm1) / (mass + other.mass);

      vx = tx * dpTan1 + nx * m1;
      vy = ty * dpTan1 + ny * m1;
      other.vx = tx * dpTan2 + nx * m2;
      other.vy = ty * dpTan2 + ny * m2;
    }
  }

  void display() {
    fill(100, 150, 255);
    stroke(0);
    ellipse(x, y, r*2, r*2);
  }
}
