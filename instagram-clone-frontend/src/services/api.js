import axios from "axios";

const API_BASE_URL = "http://localhost:8080"; // Replace with your backend URL

// Setting up a basic Axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const registerUser = async (userData) => {
  return await api.post("/user/register", userData);
};

export const signIn = async (credentials) => {
  return await api.post("/user/signIn", credentials);
};

export const signOut = async (user, token) => {
  console.log("here in signOUt = ", user);
  const response = await api.post("/user/signOut", user, {
    headers: { Authorization: token },
  });
  console.log(response);
  return response;
};

export const createPost = async (post, userId, token) => {
  // Use backticks for template literals
  const response = await api.post(`/post/${userId}`, post, {
    headers: {
      Authorization: token,
      "Content-Type": "application/json", // Optionally include content type for clarity
    },
  });

  return response;
};

// Fetch post details by ID
export const getPostById = async (postId, token) => {
  try {
    const response = await api.get(`/post/${postId}`, {
      headers: { Authorization: token },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching post details:", error);
    throw error;
  }
};

// Fetch reel details by ID
export const getReelById = async (reelId, token) => {
  try {
    const response = await api.get(`/reel/${reelId}`, {
      headers: { Authorization: token },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching reel details:", error);
    throw error;
  }
};

// Fetch posts
export const getPosts = async (token) => {
  return await api.get("/posts", { headers: { Authorization: token } });
};

// Fetch user data by ID
export const getUserById = async (userId, token) => {
  try {
    const response = await api.get(`/user/${userId}`, {
      headers: { Authorization: token },
    });

    return {
      data: response.data,
      status: response.status,
    };
  } catch (error) {
    return {
      data: null,
      status: error.status,
      message: error.message,
    };
  }
};

export const addHighlight = async (userId, highlight, token) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/user/${userId}/addHighlight`,
      highlight,
      {
        headers: {
          Authorization: `${token}`,
        },
      }
    );
    return response;
  } catch (error) {
    throw error;
  }
};

export const removeHighlight = async (userId, index, token) => {
  console.log("token at remove == ", token);
  try {
    const response = await api.delete(
      `/user/${userId}/removeHighlight/${index}`,
      {
        headers: { Authorization: token },
      }
    );

    return response;
  } catch (error) {
    throw error;
  }
};

export const updateUserProfile = async (userId, user, token) => {
  console.log("user at update == ", user);
  try {
    const response = await api.put(`/user/${userId}`, user, {
      headers: { Authorization: token },
    });

    return response;
  } catch (error) {
    throw error;
  }
};

export const likePost = async (userId, postId, token) => {
  try {
    const response = await api.get(`/post/${postId}/like/${userId}`, {
      headers: { Authorization: token },
    });

    return response;
  } catch (error) {
    throw error;
  }
};

export const unlikePost = async (userId, postId, token) => {
  try {
    const response = await api.get(`/post/${postId}/unlike/${userId}`, {
      headers: { Authorization: token },
    });

    return response;
  } catch (error) {
    throw error;
  }
};

export const addComment = async (userId, postId, message, token) => {
  try {
    const response = await api.post(
      `/post/${postId}/comment`,
      {
        userId: userId,
        message: message,
      },
      {
        headers: { Authorization: token },
      }
    );

    return response;
  } catch (error) {
    console.error("Error adding comment:", error);
    throw error;
  }
};

export const unComment = async (postId, index, token) => {
  try {
    const response = await api.get(`/post/${postId}/unComment/${index}`, {
      headers: { Authorization: token },
    });

    return response;
  } catch (error) {
    console.error("Error adding comment:", error);
    throw error;
  }
};

export const followUser = async (userId, followId, token) => {
  try {
    const response = await api.get(`user/${userId}/follow/${followId}`, {
      headers: { Authorization: token },
    });

    console.log(response);

    return response;
  } catch (error) {
    console.error("Error adding comment:", error);
    throw error;
  }
};

export const unfollowUser = async (userId, followId, token) => {
  try {
    const response = await api.get(`user/${userId}/unFollow/${followId}`, {
      headers: { Authorization: token },
    });

    console.log(response);

    return response;
  } catch (error) {
    console.error("Error adding comment:", error);
    throw error;
  }
};

// Function to get posts from followed users
export const getFollowingPosts = async (userId, token) => {
  try {
    const response = await api.get("/posts/following", {
      headers: {
        Authorization: token,
      },
      params: {
        userId: userId,
      },
    });

    console.log(response.data); // Log the response data
    return response;
  } catch (error) {
    console.error("Error fetching posts from followed users:", error);
    throw error;
  }
};

// Add other API calls as needed
