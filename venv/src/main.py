import cv2
import numpy as np

iname = "imgb-1000-5-EBA"

image = cv2.imread('src/data/ResultadosBll1000/'+iname+'.jpg')

hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

lower_orange = np.array([5, 100, 100])
lower_orange = np.array([5, 5, 5])

upper_orange = np.array([15, 255, 255])

mask = cv2.inRange(hsv, lower_orange, upper_orange)

result = cv2.bitwise_and(image, image, mask=mask)

cv2.imwrite('src/data/ResultadosBll1000/'+iname+'-ECA.jpg', result)