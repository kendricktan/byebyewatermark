import cv2
import sys
import numpy as np

# Anywhere that is not black = repainted

# Our file names
try:
    fn_watermarked = sys.argv[1]
    fn_thumbnail = sys.argv[2]
    fn_masked = sys.argv[3]
    fn_cleaned = 'cleaned_' + fn_watermarked
except:
    fn_watermarked = 'watermarked.png'
    fn_thumbnail = 'thumbnail.png'
    fn_masked = 'masked.png'
    fn_cleaned = 'clean_image.png'

im_watermarked = cv2.imread(fn_watermarked, cv2.IMREAD_COLOR)
im_thumbnail = cv2.imread(fn_thumbnail, cv2.IMREAD_COLOR)
im_masked = cv2.imread(fn_masked, cv2.IMREAD_COLOR)

# Resizing
im_thumbnail = cv2.resize(im_thumbnail, (im_watermarked.shape[1], im_watermarked.shape[0]))
im_masked = cv2.resize(im_masked, (im_watermarked.shape[1], im_watermarked.shape[0]))

# Our clean image
im_clean = im_masked.copy()

# Replace bits that are black
for x in range(0, im_watermarked.shape[0]):
    for y in range(0, im_watermarked.shape[1]):
        if (im_clean[x][y][0] == 0 and im_clean[x][y][1] == 0 and im_clean[x][y][2] == 0):
            im_clean[x][y] = im_thumbnail[x][y]

# Image reconstruction
#im_masked = cv2.cvtColor(im_masked, cv2.COLOR_BGR2GRAY)
#ret, im_thres = cv2.threshold(im_masked, 1, 255, cv2.THRESH_BINARY_INV)
#im_clean = cv2.inpaint(im_clean, im_thres, 1, cv2.INPAINT_TELEA)

cv2.imwrite(fn_cleaned, im_clean)
print('Successfully wrote {}'.format(fn_cleaned))

#cv2.waitKey()
#cv2.destroyAllWindows()