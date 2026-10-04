package edu.gsu.gisu;

import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {
    private final JdbcTemplate db;

    public WebController(JdbcTemplate db) {
        this.db = db;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model,
                           HttpSession session) {
        if (password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters.");
            return "register";
        }
        try {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            db.update("INSERT INTO customers (first_name, last_name, email, password_hash) VALUES (?, ?, ?, ?)",
                    firstName.trim(), lastName.trim(), email.trim().toLowerCase(), hash);
            Map<String, Object> user = db.queryForMap(
                    "SELECT customer_id, first_name, last_name, email, role FROM customers WHERE email = ?",
                    email.trim().toLowerCase());
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        } catch (DuplicateKeyException ex) {
            model.addAttribute("error", "An account with that email already exists.");
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        Model model, HttpSession session) {
        List<Map<String, Object>> users = db.queryForList(
                "SELECT customer_id, first_name, last_name, email, role, password_hash FROM customers WHERE email = ?",
                email.trim().toLowerCase());
        if (users.isEmpty() || !BCrypt.checkpw(password, (String) users.get(0).get("password_hash"))) {
            model.addAttribute("error", "Email or password is incorrect.");
            return "login";
        }
        Map<String, Object> user = users.get(0);
        user.remove("password_hash");
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Map<String, Object> user = currentUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("assessments", db.queryForList(
                "SELECT assessment_id, age, coverage_type, monthly_budget, household_size, created_at " +
                "FROM insurance_assessments WHERE customer_id = ? ORDER BY created_at DESC",
                user.get("customer_id")));
        return "dashboard";
    }

    @GetMapping("/assessment")
    public String assessmentPage(HttpSession session) {
        if (currentUser(session) == null) return "redirect:/login";
        return "assessment";
    }

    @PostMapping("/assessment")
    public String submitAssessment(@RequestParam int age,
                                   @RequestParam String coverageType,
                                   @RequestParam BigDecimal monthlyBudget,
                                   @RequestParam int householdSize,
                                   HttpSession session, Model model) {
        Map<String, Object> user = currentUser(session);
        if (user == null) return "redirect:/login";
        if (age < 18 || age > 110 || householdSize < 1 || householdSize > 20 ||
                monthlyBudget.compareTo(BigDecimal.ZERO) <= 0 ||
                !(coverageType.equals("individual") || coverageType.equals("family"))) {
            model.addAttribute("error", "Please check your answers and try again.");
            return "assessment";
        }
        db.update("INSERT INTO insurance_assessments " +
                        "(customer_id, age, coverage_type, monthly_budget, household_size) VALUES (?, ?, ?, ?, ?)",
                user.get("customer_id"), age, coverageType, monthlyBudget, householdSize);
        List<Map<String, Object>> plans = db.queryForList(
                "SELECT plan_id, provider_name, plan_name, coverage_type, monthly_premium, deductible, description " +
                "FROM insurance_plans WHERE coverage_type = ? AND monthly_premium <= ? ORDER BY monthly_premium",
                coverageType, monthlyBudget);
        if (plans.isEmpty()) {
            plans = db.queryForList(
                    "SELECT plan_id, provider_name, plan_name, coverage_type, monthly_premium, deductible, description " +
                    "FROM insurance_plans WHERE coverage_type = ? ORDER BY monthly_premium LIMIT 2", coverageType);
        }
        model.addAttribute("plans", plans);
        model.addAttribute("saved", true);
        return "assessment";
    }

    @GetMapping("/plans")
    public String plans(Model model) {
        model.addAttribute("plans", db.queryForList(
                "SELECT plan_id, provider_name, plan_name, coverage_type, monthly_premium, deductible, description " +
                "FROM insurance_plans ORDER BY coverage_type, monthly_premium"));
        return "plans";
    }

    private Map<String, Object> currentUser(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) user;
            return result;
        }
        return null;
    }
}
