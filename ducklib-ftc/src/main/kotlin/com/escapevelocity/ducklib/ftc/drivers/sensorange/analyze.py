# half written by chatgpt and half written by me
# don't know which half is mine and which half is chat's

from PIL import Image
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import pyperclip

# Load the image
image_path = "./pasted file3.png"
img = Image.open(image_path).convert("L").rotate(180)  # Convert to grayscale
# img.show()

# Convert image to numpy array
img_array = np.array(img)

# Find the curve by detecting dark pixels
threshold = 127  # Assuming black curve on white background
old_curve_points = np.column_stack(np.where(img_array < threshold))
py_curve_points = []
seen_points = set()
for value in old_curve_points:
    if value[1] not in seen_points:
        py_curve_points.append(value.tolist())
        seen_points.add(value[1])

curve_points = np.array(py_curve_points)

# Flip from (row, col) to (x, y)
# curve_points = np.array([[x, y] for y, x in curve_points])

# Remove duplicates by x and take the average y for each x
df = pd.DataFrame(curve_points, columns=["x", "y"])
df_grouped = df.groupby("x")["y"].min().reset_index()

# Normalize x from 0 to 50, y from 0 to 1
# x_min, x_max = 0,
y_min, y_max = 0, img.size[0]
#
# df_grouped["y"] = 50 * (df_grouped["y"] - x_min) / (x_max - x_min)
df_grouped["y"] = y_max - (df_grouped["y"])  # Invert y-axis
new_xs = range(df_grouped["x"].max() + 1)
new_ys = np.interp(new_xs, df_grouped["x"], df_grouped["y"])
# df_grouped["x"], df_grouped["y"] = df_grouped["y"], df_grouped["x"]

# Show preview
pyperclip.copy(pd.DataFrame(np.column_stack((new_xs, new_ys))).to_csv(index=False))
