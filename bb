-- ============================================================================
-- BLADE BALL LYRA  —  Football Fusion UI Edition
--   • Feature backend : Blade Ball (bb.txt)
--   • UI shell        : Football Fusion Aurora redesign (circular logo,
--                       Colorpicker / Keybind / Divider components,
--                       hardened entrance & safety)
--   • All Blade Ball features are wired to the new UI and fully functional.
-- ============================================================================

-- ============================================================================
-- BYPASS / ANTI-DETECTION MODULE (fully pcall-wrapped, optional)
-- ============================================================================
local Bypass = pcall(function()
    local Bypass = {}
    local game = game
    local pcall = pcall
    local coroutine = coroutine
    local getrawmetatable = getrawmetatable
    local setreadonly = setreadonly
    local hookfunction = hookfunction
    local newcclosure = newcclosure
    local checkcaller = checkcaller
    local getgenv = getgenv or function() return _G end
    local getsenv = getsenv or function() return nil end
    local getupvalues = getupvalues or function() return {} end
    local setupvalue = setupvalue or function() end
    
    Bypass.__active = true
    
    -- Only perform safe, non-breaking stealth measures
    local function stealthLogDisable()
        local oldPrint = print
        print = function(...)
            local msg = table.concat({...}, " ")
            if msg and (msg:match("Lyra") or msg:match("Bypass") or msg:match("inject") or msg:match("detect")) then
                return
            end
            oldPrint(...)
        end
        local oldWarn = warn
        warn = function(...)
            local msg = table.concat({...}, " ")
            if msg and (msg:match("Lyra") or msg:match("Bypass") or msg:match("inject") or msg:match("detect")) then
                return
            end
            oldWarn(...)
        end
    end
    pcall(stealthLogDisable)
    
    -- Spoof inputs with tiny random delays
    local userInput = game:GetService("UserInputService")
    if userInput then
        local oldInputBegan = userInput.InputBegan
        if oldInputBegan then
            userInput.InputBegan = function(self, ...)
                if Bypass.__active then
                    task.wait(math.random(1, 5) / 1000)
                end
                return oldInputBegan(self, ...)
            end
        end
    end
    
    -- Periodic maintenance loop (only safe re-runs)
    coroutine.wrap(function()
        while Bypass.__active do
            task.wait(15)
            pcall(stealthLogDisable)
        end
    end)()
    
    -- Identity rotation (safe)
    coroutine.wrap(function()
        while Bypass.__active do
            task.wait(45)
            pcall(setthreadidentity, math.random(2, 5))
        end
    end)()
    
    getgenv().Bypass = Bypass
    return Bypass
end)
-- If Bypass failed to load, that's fine - all features still work
if not Bypass then
    Bypass = { __active = false }
    getgenv().Bypass = Bypass
end
-- ============================================================================
-- END BYPASS MODULE
-- ============================================================================

-- Safety check for cloneref
local cloneref = cloneref or function(obj) return obj end

-- ============================================================================

-- ============================================================================
-- LYRA BLADE BALL: STRICT MOBILE BUILD
-- ============================================================================
pcall(function()
    game:GetService("StarterGui"):SetCore("SendNotification", {
        Title = "Blade Ball Lyra",
        Text = "Strict Mobile Build Active (Combined Touch/Tap Mode Loaded)",
        Duration = 5
    })
end)
-- ============================================================================

if _G.BladeBallLyraMerged then
    return warn('Already loaded.')
end
_G.BladeBallLyraMerged = true
local getgenv = getgenv or function() return _G end
notify = notify or function(title, text)
    warn(("[LyraBladeBall] %s - %s"):format(title or "Notice", text or ""))
end

local VERSION = "4.0-Lyra"

local Players           = cloneref(game:GetService('Players'))
local ReplicatedStorage = cloneref(game:GetService('ReplicatedStorage'))
local UserInputService  = cloneref(game:GetService('UserInputService'))
local RunService        = cloneref(game:GetService('RunService'))
local TweenService      = cloneref(game:GetService('TweenService'))
local Stats             = cloneref(game:GetService('Stats'))
local Debris            = cloneref(game:GetService('Debris'))
local CoreGui           = cloneref(game:GetService('CoreGui'))
local HttpService       = cloneref(game:GetService('HttpService'))
local Workspace         = cloneref(game:GetService('Workspace'))
local VIM               = cloneref(game:GetService("VirtualInputManager"))
local Lighting          = cloneref(game:GetService('Lighting'))
local ContentProvider   = cloneref(game:GetService('ContentProvider'))
local TextService       = cloneref(game:GetService('TextService'))

local LocalPlayer = Players.LocalPlayer
local Camera = Workspace.CurrentCamera or Workspace:WaitForChild("Camera", 10)

if not LocalPlayer.Character then
    LocalPlayer.CharacterAdded:Wait()
end

local Alive = Workspace:FindFirstChild("Alive") or Workspace:WaitForChild("Alive")
local Runtime = Workspace:FindFirstChild("Runtime") or Workspace:WaitForChild("Runtime")


local function safePing()
    local ok, ping = pcall(function()
        return Stats.Network.ServerStatsItem["Data Ping"]:GetValue()
    end)
    return ok and math.floor((ping or 0) + 0.5) or 0
end

getgenv().AutoParryMode = getgenv().AutoParryMode or "Combined (Mobile)"
getgenv().HoldSpamEnabled = getgenv().HoldSpamEnabled or false
getgenv().HoldSpamKey = getgenv().HoldSpamKey or "V"
getgenv().CooldownProtection = getgenv().CooldownProtection or false
getgenv().AutoAbility = getgenv().AutoAbility or false
getgenv().AutoStopEmote = getgenv().AutoStopEmote ~= false
getgenv().PendingAvatarTarget = getgenv().PendingAvatarTarget or ""
getgenv().swordModel = getgenv().swordModel or ""
getgenv().swordAnimations = getgenv().swordAnimations or ""
getgenv().swordFX = getgenv().swordFX or ""
getgenv().skinChangerEnabled = getgenv().skinChangerEnabled or false
getgenv().changeSwordModel = getgenv().changeSwordModel or false
getgenv().changeSwordAnimation = getgenv().changeSwordAnimation or false
getgenv().changeSwordFX = getgenv().changeSwordFX or false

getgenv().GG = {
    Language = {
        CheckboxEnabled = "Enabled",
        CheckboxDisabled = "Disabled",
        SliderValue = "Value",
        DropdownSelect = "Select",
        DropdownNone = "None",
        DropdownSelected = "Selected",
        ButtonClick = "Click",
        TextboxEnter = "Enter",
        ModuleEnabled = "Enabled",
        ModuleDisabled = "Disabled",
        TabGeneral = "General",
        TabSettings = "Settings",
        Loading = "Loading...",
        Error = "Error",
        Success = "Success"
    }
}

function convertStringToTable(inputString)
    local result = {}
    for value in string.gmatch(inputString, "([^,]+)") do
        local trimmedValue = value:match("^%s*(.-)%s*$")
        table.insert(result, trimmedValue)
    end
    return result
end

function convertTableToString(inputTable)
    return table.concat(inputTable, ", ")
end

local mouse = LocalPlayer:GetMouse()
local old_UI = CoreGui:FindFirstChild('BladeBallUI')

if old_UI then
    Debris:AddItem(old_UI, 0)
end

-- Per-script config root (never shared with other Lyra scripts)
local LYRA_SCRIPT_ID = "Blade Ball"
local LYRA_ROOT = "Lyra/" .. LYRA_SCRIPT_ID
local LYRA_CONFIG_FOLDER = LYRA_ROOT .. "/Configs"
local LYRA_AUTOLOAD_FILE = LYRA_ROOT .. "/Autoload.txt"
pcall(function()
    if isfolder and makefolder then
        if not isfolder("Lyra") then makefolder("Lyra") end
        if not isfolder(LYRA_ROOT) then makefolder(LYRA_ROOT) end
        if not isfolder(LYRA_CONFIG_FOLDER) then makefolder(LYRA_CONFIG_FOLDER) end
    end
end)

local Connections = setmetatable({
    disconnect = function(self, connection)
        if not self[connection] then
            return
        end
        self[connection]:Disconnect()
        self[connection] = nil
    end,
    disconnect_all = function(self)
        for _, value in pairs(self) do
            if typeof(value) ~= 'function' then
                value:Disconnect()
            end
        end
    end
}, Connections)

local Util = setmetatable({
    map = function(self, value, in_minimum, in_maximum, out_minimum, out_maximum)
        return (value - in_minimum) * (out_maximum - out_minimum) / (in_maximum - in_minimum) + out_minimum
    end,
    viewport_point_to_world = function(self, location, distance)
        local unit_ray = Workspace.CurrentCamera:ScreenPointToRay(location.X, location.Y)
        return unit_ray.Origin + unit_ray.Direction * distance
    end,
    get_offset = function(self)
        local viewport_size_Y = Workspace.CurrentCamera.ViewportSize.Y
        return self:map(viewport_size_Y, 0, 2560, 8, 56)
    end
}, Util)

local AcrylicBlur = {}
AcrylicBlur.__index = AcrylicBlur

function AcrylicBlur.new(object)
    local self = setmetatable({
        _object = object,
        _folder = nil,
        _frame = nil,
        _root = nil
    }, AcrylicBlur)
    self:setup()
    return self
end

function AcrylicBlur:create_folder()
    local old_folder = Workspace.CurrentCamera:FindFirstChild('AcrylicBlur')
    if old_folder then
        Debris:AddItem(old_folder, 0)
    end
    local folder = Instance.new('Folder')
    folder.Name = 'AcrylicBlur'
    folder.Parent = Workspace.CurrentCamera
    self._folder = folder
end

function AcrylicBlur:create_depth_of_fields()
    local depth_of_fields = Lighting:FindFirstChild('AcrylicBlur') or Instance.new('DepthOfFieldEffect')
    depth_of_fields.FarIntensity = 0
    depth_of_fields.FocusDistance = 0.05
    depth_of_fields.InFocusRadius = 0.1
    depth_of_fields.NearIntensity = 1
    depth_of_fields.Name = 'AcrylicBlur'
    depth_of_fields.Parent = Lighting
    
    for _, object in ipairs(Lighting:GetChildren()) do
        if object:IsA('DepthOfFieldEffect') and object ~= depth_of_fields then
            Connections[object] = object:GetPropertyChangedSignal('FarIntensity'):Connect(function()
                object.FarIntensity = 0
            end)
            object.FarIntensity = 0
        end
    end
end

function AcrylicBlur:create_frame()
    local frame = Instance.new('Frame')
    frame.Size = UDim2.new(1, 0, 1, 0)
    frame.Position = UDim2.new(0.5, 0, 0.5, 0)
    frame.AnchorPoint = Vector2.new(0.5, 0.5)
    frame.BackgroundTransparency = 1
    frame.Parent = self._object
    self._frame = frame
end

function AcrylicBlur:create_root()
    local part = Instance.new('Part')
    part.Name = 'Root'
    part.Color = Color3.new(0, 0, 0)
    part.Material = Enum.Material.Glass
    part.Size = Vector3.new(1, 1, 0)
    part.Anchored = true
    part.CanCollide = false
    part.CanQuery = false
    part.Locked = true
    part.CastShadow = false
    part.Transparency = 0.98
    part.Parent = self._folder
    local specialMesh = Instance.new('SpecialMesh')
    specialMesh.MeshType = Enum.MeshType.Brick
    specialMesh.Offset = Vector3.new(0, 0, -0.000001)
    specialMesh.Parent = part
    self._root = part
end

function AcrylicBlur:setup()
    self:create_depth_of_fields()
    self:create_folder()
    self:create_root()
    self:create_frame()
    self:render(0.001)
    self:check_quality_level()
end

function AcrylicBlur:render(distance)
    local positions = {
        top_left = Vector2.new(),
        top_right = Vector2.new(),
        bottom_right = Vector2.new(),
    }
    local function update_positions(size, position)
        positions.top_left = position
        positions.top_right = position + Vector2.new(size.X, 0)
        positions.bottom_right = position + size
    end
    local function update()
        local top_left = positions.top_left
        local top_right = positions.top_right
        local bottom_right = positions.bottom_right
        local top_left3D = Util:viewport_point_to_world(top_left, distance)
        local top_right3D = Util:viewport_point_to_world(top_right, distance)
        local bottom_right3D = Util:viewport_point_to_world(bottom_right, distance)
        local width = (top_right3D - top_left3D).Magnitude
        local height = (top_right3D - bottom_right3D).Magnitude
        if not self._root then
            return
        end
        self._root.CFrame = CFrame.fromMatrix((top_left3D + bottom_right3D) / 2, Workspace.CurrentCamera.CFrame.XVector, Workspace.CurrentCamera.CFrame.YVector, Workspace.CurrentCamera.CFrame.ZVector)
        self._root.Mesh.Scale = Vector3.new(width, height, 0)
    end
    local function on_change()
        local offset = Util:get_offset()
        local size = self._frame.AbsoluteSize - Vector2.new(offset, offset)
        local position = self._frame.AbsolutePosition + Vector2.new(offset / 2, offset / 2)
        update_positions(size, position)
        task.spawn(update)
    end
    Connections['cframe_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('CFrame'):Connect(update)
    Connections['viewport_size_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('ViewportSize'):Connect(update)
    Connections['field_of_view_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('FieldOfView'):Connect(update)
    Connections['frame_absolute_position'] = self._frame:GetPropertyChangedSignal('AbsolutePosition'):Connect(on_change)
    Connections['frame_absolute_size'] = self._frame:GetPropertyChangedSignal('AbsoluteSize'):Connect(on_change)
    task.spawn(update)
end

function AcrylicBlur:check_quality_level()
    local game_settings = UserSettings().GameSettings
    local quality_level = game_settings.SavedQualityLevel.Value
    if quality_level < 8 then
        self:change_visiblity(false)
    end
    Connections['quality_level'] = game_settings:GetPropertyChangedSignal('SavedQualityLevel'):Connect(function()
        local game_settings = UserSettings().GameSettings
        local quality_level = game_settings.SavedQualityLevel.Value
        self:change_visiblity(quality_level >= 8)
    end)
end

function AcrylicBlur:change_visiblity(state)
    self._root.Transparency = state and 0.98 or 1
end

local Config = setmetatable({
    save = function(self, file_name, config)
        local success_save, result = pcall(function()
            local flags = HttpService:JSONEncode(config)
            writefile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json', flags)
        end)
        if not success_save then
            warn('failed to save config', result)
        end
    end,
    load = function(self, file_name, config)
        local success_load, result = pcall(function()
            if not isfile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json') then
                self:save(file_name, config)
                return
            end
            local flags = readfile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json')
            if not flags then
                self:save(file_name, config)
                return
            end
            return HttpService:JSONDecode(flags)
        end)
        if not success_load then
            warn('failed to load config', result)
        end
        if not result then
            result = {
                _flags = {},
                _keybinds = {},
                _library = {}
            }
        end
        return result
    end
}, Config)

local System = {
    __properties = {
        __autoparry_enabled = false,
        __triggerbot_enabled = false,
        __manual_spam_enabled = false,
        __auto_spam_enabled = false,
        __play_animation = false,
        __accuracy = 1,
        __divisor_multiplier = 1.1,
        __parried = false,
        __training_parried = false,
        __spam_threshold = 1.5,
        __parries = 0,
        __grab_animation = nil,
        __tornado_time = tick(),
        __connections = {},
        __spam_accumulator = 0,
        __spam_rate = 240,
        __infinity_active = false,
        __deathslash_active = false,
        __timehole_active = false,
        __slashesoffury_active = false,
        __slashesoffury_count = 0,
        __is_mobile = UserInputService.TouchEnabled and not UserInputService.MouseEnabled,
        __mobile_guis = {}
    },
    
    __config = {
        __detections = {
            __infinity = false,
            __deathslash = false,
            __timehole = false,
            __slashesoffury = false,
            __phantom = false
        }
    },
    
    __triggerbot = {
        __enabled = false,
        __is_parrying = false,
        __parries = 0,
        __max_parries = 10000,
        __parry_delay = 0.5
    }
}

local PF = nil
local SC = nil

if ReplicatedStorage:FindFirstChild("Controllers") then
    for _, child in ipairs(ReplicatedStorage.Controllers:GetChildren()) do
        if child.Name:match("^SwordsController%s*$") then
            SC = child
        end
    end
end

if LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block") then
    for _, v in next, getconnections(LocalPlayer.PlayerGui.Hotbar.Block.Activated) do
        if SC and getfenv(v.Function).script == SC then
            PF = v.Function
            break
        end
    end
end

local function update_divisor()
    System.__properties.__divisor_multiplier = 0.75 + (System.__properties.__accuracy - 1) * (3 / 99)
end

System.animation = {}

function System.animation.play_grab_parry()
    if not System.__properties.__play_animation then return end
    
    local character = LocalPlayer.Character
    if not character then return end
    
    local humanoid = character:FindFirstChildOfClass('Humanoid')
    local animator = humanoid and humanoid:FindFirstChildOfClass('Animator')
    if not humanoid or not animator then return end
    
    local sword_name
    if getgenv().skinChangerEnabled then
        sword_name = getgenv().swordAnimations
    else
        sword_name = character:GetAttribute('CurrentlyEquippedSword')
    end
    if not sword_name then return end
    
    local sword_api = ReplicatedStorage.Shared.SwordAPI.Collection
    local parry_animation = sword_api.Default:FindFirstChild('GrabParry')
    if not parry_animation then return end
    
    local sword_data = ReplicatedStorage.Shared.ReplicatedInstances.Swords.GetSword:Invoke(sword_name)
    if not sword_data or not sword_data['AnimationType'] then return end
    
    for _, object in pairs(sword_api:GetChildren()) do
        if object.Name == sword_data['AnimationType'] then
            if object:FindFirstChild('GrabParry') or object:FindFirstChild('Grab') then
                local animation_type = object:FindFirstChild('GrabParry') and 'GrabParry' or 'Grab'
                parry_animation = object[animation_type]
            end
        end
    end
    
    if System.__properties.__grab_animation and System.__properties.__grab_animation.IsPlaying then
        System.__properties.__grab_animation:Stop()
    end
    
    System.__properties.__grab_animation = animator:LoadAnimation(parry_animation)
    System.__properties.__grab_animation.Priority = Enum.AnimationPriority.Action4
    System.__properties.__grab_animation:Play()
end

System.ball = {}

function System.ball.get()
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return nil end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            ball.CanCollide = false
            return ball
        end
    end
    return nil
end

function System.ball.get_all()
    local balls_table = {}
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return balls_table end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            ball.CanCollide = false
            table.insert(balls_table, ball)
        end
    end
    return balls_table
end

System.player = {}
local Closest_Entity = nil

function System.player.get_closest()
    local max_distance = math.huge
    local closest_entity = nil
    if not Alive then return nil end
    for _, entity in pairs(Alive:GetChildren()) do
        if entity ~= LocalPlayer.Character and entity.PrimaryPart then
            local distance = LocalPlayer:DistanceFromCharacter(entity.PrimaryPart.Position)
            if distance < max_distance then
                max_distance = distance
                closest_entity = entity
            end
        end
    end
    Closest_Entity = closest_entity
    return closest_entity
end

System.parry = {}
function System.parry.execute()
    if System.__properties.__parries > 10000 or not LocalPlayer.Character then return end
    
    local method = getgenv().AutoParryMode or "Combined (Mobile)"
    
    if method == "Combined (Mobile)" then
        -- Execute all methods for maximum reliability on mobile
        pcall(function()
            -- 1. Direct function call if found
            if PF then
                task.spawn(function() pcall(PF) end)
            end
            
            -- 2. Fire block button UI connections
            local blockButton = LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block")
            if blockButton then
                for _, eventName in ipairs({"Activated", "MouseButton1Click", "MouseButton1Down", "TouchTap"}) do
                    local event = blockButton:FindFirstChild(eventName) or blockButton[eventName]
                    if event then
                        for _, connection in ipairs(getconnections(event)) do
                            pcall(function() connection:Fire() end)
                        end
                    end
                end
            end
            
            -- 3. Screen tap emulation
            local camera = workspace.CurrentCamera
            local viewportSize = camera and camera.ViewportSize or Vector2.new(800, 600)
            local clickX, clickY = viewportSize.X / 2, viewportSize.Y / 2
            
            if VIM and VIM.SendTouchEvent then
                VIM:SendTouchEvent(1, 0, clickX, clickY) -- State 0: TouchBegan
                task.wait()
                VIM:SendTouchEvent(1, 2, clickX, clickY) -- State 2: TouchEnded
            elseif VIM and VIM.SendMouseButtonEvent then
                VIM:SendMouseButtonEvent(clickX, clickY, 0, true, game, 1)
                task.wait()
                VIM:SendMouseButtonEvent(clickX, clickY, 0, false, game, 1)
            end
        end)
    elseif method == "Screen Tap" then
        pcall(function()
            local camera = workspace.CurrentCamera
            local viewportSize = camera and camera.ViewportSize or Vector2.new(800, 600)
            local clickX, clickY = viewportSize.X / 2, viewportSize.Y / 2
            
            if VIM and VIM.SendTouchEvent then
                VIM:SendTouchEvent(1, 0, clickX, clickY)
                task.wait()
                VIM:SendTouchEvent(1, 2, clickX, clickY)
            elseif VIM and VIM.SendMouseButtonEvent then
                VIM:SendMouseButtonEvent(clickX, clickY, 0, true, game, 1)
                task.wait()
                VIM:SendMouseButtonEvent(clickX, clickY, 0, false, game, 1)
            end
        end)
    elseif method == "Block Button" then
        pcall(function()
            local blockButton = LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block")
            if blockButton then
                for _, eventName in ipairs({"Activated", "MouseButton1Click", "MouseButton1Down", "TouchTap"}) do
                    local event = blockButton:FindFirstChild(eventName) or blockButton[eventName]
                    if event then
                        for _, connection in ipairs(getconnections(event)) do
                            pcall(function() connection:Fire() end)
                        end
                    end
                end
            end
        end)
    elseif method == "Direct Call (PF)" then
        if PF then
            pcall(PF)
        end
    elseif method == "KeyPress (F)" then
        pcall(function()
            if keypress and keyrelease then
                keypress(0x46)
                task.wait()
                keyrelease(0x46)
            else
                VIM:SendKeyEvent(true, Enum.KeyCode.F, false, game)
                task.wait()
                VIM:SendKeyEvent(false, Enum.KeyCode.F, false, game)
            end
        end)
    elseif method == "MouseClick" then
        if mouse1click then
            pcall(mouse1click)
        else
            pcall(function()
                VIM:SendMouseButtonEvent(0, 0, 0, true, game, 0)
                task.wait()
                VIM:SendMouseButtonEvent(0, 0, 0, false, game, 0)
            end)
        end
    end
    
    if System.__properties.__parries <= 10000 then
        System.__properties.__parries = System.__properties.__parries + 1
        task.delay(0.5, function()
            if System.__properties.__parries > 0 then System.__properties.__parries = System.__properties.__parries - 1 end
        end)
    end
end

function System.parry.keypress()
    System.parry.execute()
end

function System.parry.execute_action()
    System.animation.play_grab_parry()
    System.parry.execute()
end

local function linear_predict(a, b, time_volume) return a + (b - a) * time_volume end

System.detection = {
    __ball_properties = { __aerodynamic_time = tick(), __last_warping = tick(), __lerp_radians = 0, __curving = tick() }
}

function System.detection.is_curved()
    local ball_properties = System.detection.__ball_properties
    local ball = System.ball.get()
    if not ball then return false end
    
    local zoomies = ball:FindFirstChild('zoomies')
    if not zoomies then return false end
    
    local velocity = zoomies.VectorVelocity
    local ball_direction = velocity.Unit
    local direction = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit
    local dot = direction:Dot(ball_direction)
    
    local speed = velocity.Magnitude
    local speed_threshold = math.min(speed / 100, 40)
    
    local direction_difference = (ball_direction - velocity).Unit
    local direction_similarity = direction:Dot(direction_difference)
    local dot_difference = dot - direction_similarity
    local distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude
    
    local ping = Stats.Network.ServerStatsItem['Data Ping']:GetValue()
    local dot_threshold = 0.5 - (ping / 1000)
    local reach_time = distance / speed - (ping / 1000)
    local ball_distance_threshold = 15 - math.min(distance / 1000, 15) + speed_threshold
    
    local clamped_dot = math.clamp(dot, -1, 1)
    local radians = math.rad(math.asin(clamped_dot))
    
    ball_properties.__lerp_radians = linear_predict(ball_properties.__lerp_radians, radians, 0.8)
    
    if speed > 0 and reach_time > ping / 10 then ball_distance_threshold = math.max(ball_distance_threshold - 15, 15) end
    if distance < ball_distance_threshold then return false end
    if dot_difference < dot_threshold then return true end
    
    if ball_properties.__lerp_radians < 0.018 then ball_properties.__last_warping = tick() end
    if (tick() - ball_properties.__last_warping) < (reach_time / 1.5) then return true end
    if (tick() - ball_properties.__curving) < (reach_time / 1.5) then return true end
    
    return dot < dot_threshold
end

ReplicatedStorage.Remotes.DeathBall.OnClientEvent:Connect(function(_, d) System.__properties.__deathslash_active = d or false end)
ReplicatedStorage.Remotes.InfinityBall.OnClientEvent:Connect(function(_, b) System.__properties.__infinity_active = b or false end)

pcall(function()
    local net = ReplicatedStorage.Packages._Index["sleitnick_net@0.1.0"].net
    net["RE/TimeHoleActivate"].OnClientEvent:Connect(function(player)
        if player == LocalPlayer or player == LocalPlayer.Name or (player and player.Name == LocalPlayer.Name) then
            System.__properties.__timehole_active = true
        end
    end)
    net["RE/TimeHoleDeactivate"].OnClientEvent:Connect(function() System.__properties.__timehole_active = false end)
    
    net["RE/SlashesOfFuryActivate"].OnClientEvent:Connect(function(player)
        if player == LocalPlayer or player == LocalPlayer.Name or (player and player.Name == LocalPlayer.Name) then
            System.__properties.__slashesoffury_active = true
            System.__properties.__slashesoffury_count = 0
        end
    end)
    net["RE/SlashesOfFuryEnd"].OnClientEvent:Connect(function()
        System.__properties.__slashesoffury_active = false
        System.__properties.__slashesoffury_count = 0
    end)
    net["RE/SlashesOfFuryParry"].OnClientEvent:Connect(function() System.__properties.__slashesoffury_count = System.__properties.__slashesoffury_count + 1 end)
    net["RE/SlashesOfFuryCatch"].OnClientEvent:Connect(function()
        task.spawn(function()
            while System.__properties.__slashesoffury_active and System.__properties.__slashesoffury_count < 36 do
                if System.__config.__detections.__slashesoffury then
                    System.parry.execute()
                    task.wait(0.05)
                else break end
            end
        end)
    end)
end)

Runtime.ChildAdded:Connect(function(Object)
    if System.__config.__detections.__phantom and (Object.Name == "maxTransmission" or Object.Name == "transmissionpart") then
        local Weld = Object:FindFirstChildWhichIsA("WeldConstraint")
        local Character = LocalPlayer.Character
        if Weld and Character and Weld.Part1 == Character:FindFirstChild("HumanoidRootPart") then
            local CurrentBall = System.ball.get()
            Weld:Destroy()
            if CurrentBall then
                local FocusConnection
                FocusConnection = RunService.RenderStepped:Connect(function()
                    local Highlighted = CurrentBall:GetAttribute("highlighted")
                    if Highlighted == true then
                        ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                        System.__properties.__parried = true
                        task.delay(1, function() System.__properties.__parried = false end)
                    elseif Highlighted == false then
                        FocusConnection:Disconnect()
                    end
                end)
                task.delay(3, function() if FocusConnection and FocusConnection.Connected then FocusConnection:Disconnect() end end)
            end
        end
    end
end)

System.triggerbot = {}
function System.triggerbot.trigger(ball)
    if System.__triggerbot.__is_parrying or System.__triggerbot.__parries > System.__triggerbot.__max_parries then return end
    if LocalPlayer.Character and LocalPlayer.Character.PrimaryPart and LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape') then return end
    
    System.__triggerbot.__is_parrying = true
    System.__triggerbot.__parries = System.__triggerbot.__parries + 1
    
    System.animation.play_grab_parry()
    System.parry.execute()
    
    task.delay(System.__triggerbot.__parry_delay, function()
        if System.__triggerbot.__parries > 0 then System.__triggerbot.__parries = System.__triggerbot.__parries - 1 end
    end)
    
    local connection
    connection = ball:GetAttributeChangedSignal('target'):Once(function()
        System.__triggerbot.__is_parrying = false
        if connection then connection:Disconnect() end
    end)
    
    task.spawn(function()
        local start_time = tick()
        repeat RunService.Heartbeat:Wait() until (tick() - start_time >= 1 or not System.__triggerbot.__is_parrying)
        System.__triggerbot.__is_parrying = false
    end)
end

function System.triggerbot.loop()
    if not System.__triggerbot.__enabled then return end
    if LocalPlayer.Character and LocalPlayer.Character.PrimaryPart and LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape') then return end
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:IsA('BasePart') and ball:GetAttribute('target') == LocalPlayer.Name then
            System.triggerbot.trigger(ball)
            break
        end
    end
end

function System.triggerbot.enable(enabled)
    System.__triggerbot.__enabled = enabled
    if enabled then
        if not System.__properties.__connections.__triggerbot then System.__properties.__connections.__triggerbot = RunService.Heartbeat:Connect(System.triggerbot.loop) end
    else
        if System.__properties.__connections.__triggerbot then
            System.__properties.__connections.__triggerbot:Disconnect()
            System.__properties.__connections.__triggerbot = nil
        end
        System.__triggerbot.__is_parrying = false
        System.__triggerbot.__parries = 0
    end
end

System.manual_spam = {}
getgenv().is_holding_spam_key = false

UserInputService.InputBegan:Connect(function(input, gp)
    if gp then return end
    if input.UserInputType == Enum.UserInputType.Keyboard then
        local keyName = input.KeyCode.Name
        if keyName == (getgenv().HoldSpamKey or "V") then
            getgenv().is_holding_spam_key = true
        end
    end
end)

UserInputService.InputEnded:Connect(function(input, gp)
    if input.UserInputType == Enum.UserInputType.Keyboard then
        local keyName = input.KeyCode.Name
        if keyName == (getgenv().HoldSpamKey or "V") then
            getgenv().is_holding_spam_key = false
        end
    end
end)

function System.manual_spam.loop(delta)
    local should_spam = System.__properties.__manual_spam_enabled or (getgenv().HoldSpamEnabled and getgenv().is_holding_spam_key)
    
    if not should_spam or not LocalPlayer.Character or LocalPlayer.Character.Parent ~= Alive then return end
    System.__properties.__spam_accumulator = System.__properties.__spam_accumulator + delta
    local interval = 1 / System.__properties.__spam_rate
    if System.__properties.__spam_accumulator >= interval then
        System.__properties.__spam_accumulator = 0
        System.parry.execute()
        if getgenv().ManualSpamAnimationFix and PF then pcall(PF) end
    end
end

RunService.Heartbeat:Connect(System.manual_spam.loop)

System.auto_spam = {}
function System.auto_spam:get_entity_properties()
    System.player.get_closest()
    if not Closest_Entity then return false end
    return { Velocity = Closest_Entity.PrimaryPart.Velocity, Direction = (LocalPlayer.Character.PrimaryPart.Position - Closest_Entity.PrimaryPart.Position).Unit, Distance = (LocalPlayer.Character.PrimaryPart.Position - Closest_Entity.PrimaryPart.Position).Magnitude }
end

function System.auto_spam:get_ball_properties()
    local ball = System.ball.get()
    if not ball then return false end
    local ball_velocity = Vector3.zero
    local ball_direction = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit
    return { Velocity = ball_velocity, Direction = ball_direction, Distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude, Dot = ball_direction:Dot(ball_velocity.Unit) }
end

function System.auto_spam.spam_service(self)
    local ball = System.ball.get()
    local entity = System.player.get_closest()
    if not ball or not entity or not entity.PrimaryPart then return 0 end
    
    local speed = ball.AssemblyLinearVelocity.Magnitude
    local dot = ((LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit):Dot(ball.AssemblyLinearVelocity.Unit)
    local target_distance = LocalPlayer:DistanceFromCharacter(entity.PrimaryPart.Position)
    local maximum_spam_distance = self.Ping + math.min(speed / 6, 255)
    
    if self.Entity_Properties.Distance > maximum_spam_distance or self.Ball_Properties.Distance > maximum_spam_distance or target_distance > maximum_spam_distance then return 0 end
    local maximum_dot = math.clamp(dot, -1, 0) * (5 - math.min(speed / 5, 5))
    return maximum_spam_distance - maximum_dot
end

function System.auto_spam.start()
    if System.__properties.__connections.__auto_spam then System.__properties.__connections.__auto_spam:Disconnect() end
    System.__properties.__auto_spam_enabled = true
    System.__properties.__connections.__auto_spam = RunService.PreSimulation:Connect(function()
        local ball = System.ball.get()
        if not ball or System.__properties.__slashesoffury_active then return end
        local zoomies = ball:FindFirstChild('zoomies')
        if not zoomies then return end
        
        System.player.get_closest()
        local ping = Stats.Network.ServerStatsItem['Data Ping']:GetValue()
        local ping_threshold = math.clamp(ping / 10, 1, 16)
        local ball_target = ball:GetAttribute('target')
        local ball_props = System.auto_spam:get_ball_properties()
        local entity_props = System.auto_spam:get_entity_properties()
        
        if not ball_props or not entity_props or not ball_target then return end
        local spam_accuracy = System.auto_spam.spam_service({Ball_Properties = ball_props, Entity_Properties = entity_props, Ping = ping_threshold})
        local target_distance = LocalPlayer:DistanceFromCharacter(Closest_Entity.PrimaryPart.Position)
        local distance = LocalPlayer:DistanceFromCharacter(ball.Position)
        
        if target_distance > spam_accuracy or distance > spam_accuracy then return end
        if LocalPlayer.Character:GetAttribute('Pulsed') then return end
        if ball_target == LocalPlayer.Name and target_distance > 30 and distance > 30 then return end
        
        if distance <= spam_accuracy and System.__properties.__parries > System.__properties.__spam_threshold then
            System.parry.execute()
            if getgenv().AutoSpamAnimationFix and PF then pcall(PF) end
        end
    end)
end

function System.auto_spam.stop()
    System.__properties.__auto_spam_enabled = false
    if System.__properties.__connections.__auto_spam then System.__properties.__connections.__auto_spam:Disconnect(); System.__properties.__connections.__auto_spam = nil end
end

System.autoparry = {}
function System.autoparry.start()
    if System.__properties.__connections.__autoparry then System.__properties.__connections.__autoparry:Disconnect() end
    System.__properties.__connections.__autoparry = RunService.PreSimulation:Connect(function()
        if not System.__properties.__autoparry_enabled or not LocalPlayer.Character or not LocalPlayer.Character.PrimaryPart then return end
        
        local balls = System.ball.get_all()
        local one_ball = System.ball.get()
        local training_ball = Workspace:FindFirstChild("TrainingBalls") and Workspace.TrainingBalls:FindFirstChildWhichIsA("BasePart")
        
        for _, ball in pairs(balls) do
            local skip = false
            
            if System.__triggerbot.__enabled or getgenv().BallVelocityAbove800 or not ball then 
                skip = true 
            end
            
            local zoomies
            if not skip then
                zoomies = ball:FindFirstChild('zoomies')
                if not zoomies then skip = true end
            end
            
            if not skip then
                ball:GetAttributeChangedSignal('target'):Once(function() System.__properties.__parried = false end)
                if System.__properties.__parried then skip = true end
            end
            
            if not skip then
                local ball_target = ball:GetAttribute('target')
                local velocity = zoomies.VectorVelocity
                local distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude
                local ping_threshold = math.clamp(Stats.Network.ServerStatsItem['Data Ping']:GetValue() / 100, 5, 17)
                local speed = velocity.Magnitude
                
                local speed_divisor = (2.4 + math.min(math.max(speed - 9.5, 0), 650) * 0.002) * System.__properties.__divisor_multiplier
                local parry_accuracy = ping_threshold + math.max(speed / speed_divisor, 9.5)
                local curved = System.detection.is_curved()
                
                if ball:FindFirstChild('AeroDynamicSlashVFX') then 
                    ball.AeroDynamicSlashVFX:Destroy()
                    System.__properties.__tornado_time = tick() 
                end
                
                if Runtime:FindFirstChild('Tornado') and (tick() - System.__properties.__tornado_time) < (Runtime.Tornado:GetAttribute('TornadoTime') or 1) + 0.314159 then skip = true end
                if not skip and one_ball and one_ball:GetAttribute('target') == LocalPlayer.Name and curved then skip = true end
                if not skip and (ball:FindFirstChild('ComboCounter') or LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape')) then skip = true end
                if not skip and ((System.__config.__detections.__infinity and System.__properties.__infinity_active) or (System.__config.__detections.__deathslash and System.__properties.__deathslash_active) or (System.__config.__detections.__timehole and System.__properties.__timehole_active) or (System.__config.__detections.__slashesoffury and System.__properties.__slashesoffury_active)) then skip = true end
                
                if not skip and ball_target == LocalPlayer.Name and distance <= parry_accuracy then
                    if getgenv().CooldownProtection and LocalPlayer.PlayerGui.Hotbar.Block.UIGradient.Offset.Y < 0.4 then 
                        ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                        skip = true 
                    end
                    
                    if not skip and getgenv().AutoAbility and LocalPlayer.PlayerGui.Hotbar.Ability.UIGradient.Offset.Y == 0.5 then
                        local abs = LocalPlayer.Character:FindFirstChild("Abilities")
                        if abs then
                            for _, ab in pairs({"Raging Deflection", "Rapture", "Calming Deflection", "Aerodynamic Slash", "Fracture", "Death Slash"}) do
                                if abs:FindFirstChild(ab) and abs[ab].Enabled then
                                    System.__properties.__parried = true
                                    ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                                    task.wait(2.432)
                                    ReplicatedStorage:WaitForChild("Remotes"):WaitForChild("DeathSlashShootActivation"):FireServer(true)
                                    break
                                end
                            end
                            if System.__properties.__parried then skip = true end
                        end
                    end
                    
                    if not skip then
                        System.parry.execute_action()
                        System.__properties.__parried = true
                    end
                end
                
                if not skip then
                    local last_parrys = tick()
                    repeat RunService.Stepped:Wait() until (tick() - last_parrys) >= 1 or not System.__properties.__parried
                    System.__properties.__parried = false
                end
            end
        end

        if training_ball and training_ball:GetAttribute("realBall") then
            local zoomies = training_ball:FindFirstChild('zoomies')
            if zoomies then
                training_ball:GetAttributeChangedSignal('target'):Once(function() System.__properties.__training_parried = false end)
                if not System.__properties.__training_parried then
                    local speed = zoomies.VectorVelocity.Magnitude
                    local distance = LocalPlayer:DistanceFromCharacter(training_ball.Position)
                    local ping_threshold = math.clamp(Stats.Network.ServerStatsItem['Data Ping']:GetValue() / 100, 5, 17)
                    local speed_divisor = (2.4 + math.min(math.max(speed - 9.5, 0), 650) * 0.002) * System.__properties.__divisor_multiplier
                    
                    if training_ball:GetAttribute('target') == LocalPlayer.Name and distance <= (ping_threshold + math.max(speed / speed_divisor, 9.5)) then
                        System.parry.execute_action()
                        System.__properties.__training_parried = true
                        local last_parrys = tick()
                        repeat RunService.Stepped:Wait() until (tick() - last_parrys) >= 1 or not System.__properties.__training_parried
                        System.__properties.__training_parried = false
                    end
                end
            end
        end
    end)
end

function System.autoparry.stop()
    if System.__properties.__connections.__autoparry then System.__properties.__connections.__autoparry:Disconnect(); System.__properties.__connections.__autoparry = nil end
end

local animation_system = {
    storage = {},
    current = nil,
    track = nil
}

function animation_system.load_animations()
    pcall(function()
        local function find_emotes(parent)
            for _, child in ipairs(parent:GetChildren()) do
                if child:IsA("Folder") and child.Name:lower() == "emotes" then
                    return child
                else
                    local found = find_emotes(child)
                    if found then return found end
                end
            end
            return nil
        end
        
        local emotes_folder = find_emotes(ReplicatedStorage)
        
        if emotes_folder then
            for _, animation in ipairs(emotes_folder:GetChildren()) do
                if animation:IsA("Animation") then
                    local emote_name = animation:GetAttribute("EmoteName") or animation.Name
                    animation_system.storage[emote_name] = animation
                end
            end
        else
            for _, obj in ipairs(ReplicatedStorage:GetDescendants()) do
                if obj:IsA("Animation") and (obj.Name:lower():match("emote") or obj.Name:lower():match("dance") or obj.Name:lower():match("floss")) then
                    local emote_name = obj:GetAttribute("EmoteName") or obj.Name
                    animation_system.storage[emote_name] = obj
                end
            end
        end
    end)
end

function animation_system.get_emotes_list()
    -- Hardcoded fallback list ensuring dropdown ALWAYS works
    local emotes_list = {"None", "Sit", "DefaultDance", "Floss", "Griddy", "TakeTheL", "Goku", "Lobby", "Dance", "Clap", "SideStep", "Shuffle", "Tantrum", "Robot", "SillyDance", "Spin", "Hypnotize", "Moonwalk"}
    
    for emote_name in pairs(animation_system.storage) do
        local found = false
        for _, existing in ipairs(emotes_list) do
            if string.lower(existing) == string.lower(emote_name) then found = true; break; end
        end
        if not found then table.insert(emotes_list, emote_name) end
    end
    
    table.sort(emotes_list)
    
    -- Guarantee 'None' stays strictly at the top of the array
    local none_idx = nil
    for i, v in ipairs(emotes_list) do
        if v == "None" then
            none_idx = i
            break
        end
    end
    if none_idx then table.remove(emotes_list, none_idx) end
    table.insert(emotes_list, 1, "None")
    
    return emotes_list
end

function animation_system.play(emote_name)
    local animation_data = animation_system.storage[emote_name]
    
    -- Dynamic aggressive search if emote isn't in storage yet but is requested via fallback/search
    if not animation_data then
        for _, obj in ipairs(ReplicatedStorage:GetDescendants()) do
            if obj:IsA("Animation") then
                local en = obj:GetAttribute("EmoteName") or obj.Name
                if string.lower(en) == string.lower(emote_name) then
                    animation_data = obj
                    animation_system.storage[emote_name] = obj
                    break
                end
            end
        end
    end
    
    if not animation_data or not LocalPlayer.Character then return false end
    
    local humanoid = LocalPlayer.Character:FindFirstChild("Humanoid")
    if not humanoid then return false end
    
    local animator = humanoid:FindFirstChild("Animator")
    if not animator then return false end
    
    if animation_system.track then
        animation_system.track:Stop()
        animation_system.track:Destroy()
    end
    
    animation_system.track = animator:LoadAnimation(animation_data)
    animation_system.track:Play()
    animation_system.current = emote_name
    return true
end

function animation_system.stop()
    if animation_system.track then
        animation_system.track:Stop()
        animation_system.track:Destroy()
        animation_system.track = nil
    end
    animation_system.current = nil
end

function animation_system.start()
    if not System.__properties.__connections.animations then
        System.__properties.__connections.animations = RunService.Heartbeat:Connect(function()
            if not LocalPlayer.Character or not LocalPlayer.Character.PrimaryPart then return end
            
            local speed = LocalPlayer.Character.PrimaryPart.AssemblyLinearVelocity.Magnitude
            if speed > 30 and getgenv().AutoStopEmote then
                if animation_system.track and animation_system.track.IsPlaying then
                    animation_system.track:Stop()
                end
            else
                if animation_system.current and (not animation_system.track or not animation_system.track.IsPlaying) then
                    animation_system.play(animation_system.current)
                end
            end
        end)
    end
end

function animation_system.cleanup()
    animation_system.stop()
    if System.__properties.__connections.animations then
        System.__properties.__connections.animations:Disconnect()
        System.__properties.__connections.animations = nil
    end
end

animation_system.load_animations()
local emotes_data = animation_system.get_emotes_list()

local WalkableSemiImmortal = {}
local immortalState = { enabled = false, notify = false, heartbeatConnection = nil }
local immortalDesyncData = { originalCFrame = nil, originalVelocity = nil }
local immortalCache = { character = nil, hrp = nil, head = nil, headOffset = Vector3.new(0, 0, 0), aliveFolder = nil }
local immortalHooks = { oldIndex = nil }
local immortalConstants = { emptyCFrame = CFrame.new(), radius = 25, baseHeight = 5, riseHeight = 30, cycleSpeed = 11.9, velocity = Vector3.new(1, 1, 1) }

local function immortalUpdateCache()
    local character = LocalPlayer.Character
    if not character then
        immortalCache.character = nil
        immortalCache.hrp = nil
        immortalCache.head = nil
        return
    end
    
    if character ~= immortalCache.character or not immortalCache.hrp or not immortalCache.head then
        immortalCache.character = character
        immortalCache.hrp = character:FindFirstChild("HumanoidRootPart")
        immortalCache.head = character:FindFirstChild("Head")
        
        if immortalCache.hrp then
            immortalCache.headOffset = Vector3.new(0, immortalCache.hrp.Size.Y * 0.5 + 0.5, 0)
        end
    end
end

local function immortalIsInAliveFolder()
    local aliveFolder = Workspace:FindFirstChild("Alive")
    return aliveFolder and immortalCache.character and immortalCache.character.Parent == aliveFolder
end

local function immortalCalculateOrbitPosition(hrp)
    local angle = math.random(-2147483647, 2147483647) * 1000
    local cycle = math.floor(tick() * immortalConstants.cycleSpeed) % 2
    local yOffset = cycle == 0 and 0 or immortalConstants.riseHeight
    
    local pos = hrp.Position
    local yBase = pos.Y - hrp.Size.Y * 0.5 + immortalConstants.baseHeight + yOffset
    
    return CFrame.new(pos.X + math.cos(angle) * immortalConstants.radius, yBase, pos.Z + math.sin(angle) * immortalConstants.radius)
end

local function performImmortalDesync()
    immortalUpdateCache()
    if not immortalState.enabled or not immortalCache.hrp or not immortalIsInAliveFolder() then return end
    
    local hrp = immortalCache.hrp
    immortalDesyncData.originalCFrame = hrp.CFrame
    immortalDesyncData.originalVelocity = hrp.AssemblyLinearVelocity
    
    hrp.CFrame = immortalCalculateOrbitPosition(hrp)
    hrp.AssemblyLinearVelocity = immortalConstants.velocity
    
    RunService.RenderStepped:Wait()
    
    hrp.CFrame = immortalDesyncData.originalCFrame
    hrp.AssemblyLinearVelocity = immortalDesyncData.originalVelocity
end

function WalkableSemiImmortal.toggle(enabled)
    if immortalState.enabled == enabled then return end
    immortalState.enabled = enabled
    getgenv().Walkablesemiimortal = enabled
    
    if enabled then
        if not immortalState.heartbeatConnection then
            immortalState.heartbeatConnection = RunService.Heartbeat:Connect(performImmortalDesync)
        end
    else
        if immortalState.heartbeatConnection then
            immortalState.heartbeatConnection:Disconnect()
            immortalState.heartbeatConnection = nil
        end
        immortalDesyncData.originalCFrame = nil
        immortalDesyncData.originalVelocity = nil
    end
end

function WalkableSemiImmortal.setRadius(value) immortalConstants.radius = value end
function WalkableSemiImmortal.setHeight(value) immortalConstants.riseHeight = value end

LocalPlayer.CharacterRemoving:Connect(function()
    immortalCache.character = nil
    immortalCache.hrp = nil
    immortalCache.head = nil
    immortalCache.aliveFolder = nil
    immortalDesyncData.originalCFrame = nil
    immortalDesyncData.originalVelocity = nil
end)

immortalHooks.oldIndex = hookmetamethod(game, "__index", newcclosure(function(self, key)
    if not checkcaller() then
        if immortalState.enabled and key == "CFrame" and immortalCache.hrp and immortalIsInAliveFolder() then
            if self == immortalCache.hrp then
                return immortalDesyncData.originalCFrame or immortalConstants.emptyCFrame
            elseif self == immortalCache.head and immortalDesyncData.originalCFrame then
                return immortalDesyncData.originalCFrame + immortalCache.headOffset
            end
        end
    end
    
    return immortalHooks.oldIndex(self, key)
end))

task.spawn(function()
    local swordInstancesInstance = ReplicatedStorage:FindFirstChild("Shared") and ReplicatedStorage.Shared:FindFirstChild("ReplicatedInstances") and ReplicatedStorage.Shared.ReplicatedInstances:FindFirstChild("Swords")
    if not swordInstancesInstance then return end
    
    local swordInstances = require(swordInstancesInstance)

    local swordsController

    while task.wait(1) and (not swordsController) do
        for i,v in getconnections(ReplicatedStorage.Remotes.FireSwordInfo.OnClientEvent) do
            if v.Function and islclosure(v.Function) then
                local upvalues = getupvalues(v.Function)
                if #upvalues == 1 and type(upvalues[1]) == "table" then
                    swordsController = upvalues[1]
                    break
                end
            end
        end
    end

    function getSlashName(swordName)
        local slashName = nil
        pcall(function() slashName = swordInstances:GetSword(swordName) end)
        return (slashName and slashName.SlashName) or "SlashEffect"
    end

    function setSword()
        if not getgenv().skinChangerEnabled then return end
        
        local char = LocalPlayer.Character
        if not char then return end

        pcall(function() 
            if swordInstances and rawget(swordInstances, "EquipSwordTo") then
                setupvalue(rawget(swordInstances,"EquipSwordTo"), 3, false) 
            end
        end)
        
        if getgenv().changeSwordModel then
            pcall(function() 
                char:SetAttribute("CurrentlyEquippedSword", getgenv().swordModel)
                swordInstances:EquipSwordTo(char, getgenv().swordModel) 
            end)
        end
        
        if getgenv().changeSwordAnimation and swordsController then
            pcall(function() swordsController:SetSword(getgenv().swordAnimations) end)
        end
    end

    local playParryFunc
    local parrySuccessAllConnection

    while task.wait() and not parrySuccessAllConnection do
        for i,v in getconnections(ReplicatedStorage.Remotes.ParrySuccessAll.OnClientEvent) do
            if v.Function and getinfo(v.Function).name == "parrySuccessAll" then
                parrySuccessAllConnection = v
                playParryFunc = v.Function
                v:Disable()
            end
        end
    end

    local parrySuccessClientConnection
    while task.wait() and not parrySuccessClientConnection do
        for i,v in getconnections(ReplicatedStorage.Remotes.ParrySuccessClient.Event) do
            if v.Function and getinfo(v.Function).name == "parrySuccessAll" then
                parrySuccessClientConnection = v
                v:Disable()
            end
        end
    end

    getgenv().slashName = getSlashName(getgenv().swordFX or "Default")

    local lastOtherParryTimestamp = 0
    ReplicatedStorage.Remotes.ParrySuccessAll.OnClientEvent:Connect(function(...)
        setthreadidentity(2)
        local args = {...}
        if tostring(args[4]) ~= LocalPlayer.Name then
            lastOtherParryTimestamp = tick()
        elseif getgenv().skinChangerEnabled and getgenv().changeSwordFX then
            args[1] = getgenv().slashName
            args[3] = getgenv().swordFX
        end
        return playParryFunc(unpack(args))
    end)

    getgenv().updateSword = function()
        if getgenv().changeSwordFX then
            getgenv().slashName = getSlashName(getgenv().swordFX or "Default")
        end
        setSword()
    end

    task.spawn(function()
        while task.wait(1) do
            if getgenv().skinChangerEnabled and getgenv().changeSwordModel then
                local char = LocalPlayer.Character or LocalPlayer.CharacterAdded:Wait()
                if LocalPlayer:GetAttribute("CurrentlyEquippedSword") ~= getgenv().swordModel then
                    setSword()
                end
                if char and (not char:FindFirstChild(getgenv().swordModel)) then
                    setSword()
                end
                for _,v in (char and char:GetChildren()) or {} do
                    if v:IsA("Model") and v.Name ~= getgenv().swordModel then
                        v:Destroy()
                    end
                    task.wait()
                end
            end
        end
    end)
end)

-- ============================================================================
-- VISUALS, ESP, PLAYER MODS
-- ============================================================================
System.visuals = {
    __esp_enabled = false, __esp_team_check = false, __esp_color = Color3.fromRGB(135, 80, 255), __esp_highlight_cache = {},
    __night_mode = false, __night_color = Color3.fromRGB(10, 10, 15),
    __original_lighting = { Ambient = Lighting.Ambient, Brightness = Lighting.Brightness, ClockTime = Lighting.ClockTime }
}

function System.visuals.update_esp()
    if not System.visuals.__esp_enabled then
        for p, h in pairs(System.visuals.__esp_highlight_cache) do h:Destroy(); System.visuals.__esp_highlight_cache[p] = nil end
        return
    end
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer and player.Character and player.Character:FindFirstChild("HumanoidRootPart") then
            if System.visuals.__esp_team_check and player.Team == LocalPlayer.Team then
                if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy(); System.visuals.__esp_highlight_cache[player] = nil end
            else
                if not System.visuals.__esp_highlight_cache[player] or System.visuals.__esp_highlight_cache[player].Parent ~= player.Character then
                    if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy() end
                    local hl = Instance.new("Highlight")
                    hl.Name = "LyraESP_" .. player.Name; hl.FillColor = System.visuals.__esp_color; hl.OutlineColor = Color3.new(1, 1, 1); hl.FillTransparency = 0.5; hl.OutlineTransparency = 0.2; hl.DepthMode = Enum.HighlightDepthMode.AlwaysOnTop; hl.Parent = player.Character
                    System.visuals.__esp_highlight_cache[player] = hl
                end
                System.visuals.__esp_highlight_cache[player].FillColor = System.visuals.__esp_color
            end
        else
            if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy(); System.visuals.__esp_highlight_cache[player] = nil end
        end
    end
end

function System.visuals.toggle_night_mode(state)
    System.visuals.__night_mode = state
    if state then Lighting.Ambient = System.visuals.__night_color; Lighting.Brightness = 0.2; Lighting.ClockTime = 0
    else Lighting.Ambient = System.visuals.__original_lighting.Ambient; Lighting.Brightness = System.visuals.__original_lighting.Brightness; Lighting.ClockTime = System.visuals.__original_lighting.ClockTime end
end

System.player_mods = { __walkspeed_enabled = false, __walkspeed_value = 16, __jumppower_enabled = false, __jumppower_value = 50, __fov_enabled = false, __fov_value = 70, __spinbot_enabled = false, __spinbot_speed = 50, __cam_shake_disabled = false }
function System.player_mods.update_movement()
    local humanoid = LocalPlayer.Character and LocalPlayer.Character:FindFirstChildOfClass("Humanoid")
    if not humanoid then return end
    if System.player_mods.__walkspeed_enabled then humanoid.WalkSpeed = System.player_mods.__walkspeed_value end
    if System.player_mods.__jumppower_enabled then humanoid.UseJumpPower = true; humanoid.JumpPower = System.player_mods.__jumppower_value end
end
function System.player_mods.update_fov() Camera.FieldOfView = System.player_mods.__fov_enabled and System.player_mods.__fov_value or 70 end
function System.player_mods.spinbot_loop()
    local hrp = LocalPlayer.Character and LocalPlayer.Character:FindFirstChild("HumanoidRootPart")
    if hrp and System.player_mods.__spinbot_enabled then hrp.CFrame = hrp.CFrame * CFrame.Angles(0, math.rad(System.player_mods.__spinbot_speed), 0) end
end
function System.player_mods.disable_camera_shake(state)
    System.player_mods.__cam_shake_disabled = state
    if state then pcall(function() for _, scr in pairs(LocalPlayer:WaitForChild("PlayerScripts"):GetDescendants()) do if scr.Name:lower():find("shake") and scr:IsA("ModuleScript") then local s, m = pcall(require, scr); if s and type(m)=="table" and m.Shake then m.Shake = function() end end end end end) end
end

RunService.RenderStepped:Connect(function()
    System.visuals.update_esp()
    System.player_mods.update_fov()
    System.player_mods.spinbot_loop()
    if System.player_mods.__walkspeed_enabled or System.player_mods.__jumppower_enabled then System.player_mods.update_movement() end
end)

local Features = {}

Features.TargetPlayer = {
    Enabled = false,
    SelectedTarget = nil,
    PlayerMap = {}
}

function Features.TargetPlayer.updatePlayerList()
    table.clear(Features.TargetPlayer.PlayerMap)
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer then
            Features.TargetPlayer.PlayerMap[player.DisplayName] = player.Name
            Features.TargetPlayer.PlayerMap[player.Name] = player.Name
        end
    end
end

function Features.TargetPlayer.setTarget(playerName)
    if not playerName or playerName == "" or playerName == "None" then
        Features.TargetPlayer.SelectedTarget = nil
        getgenv().SelectedTarget = nil
        return
    end
    
    local lowerName = string.lower(playerName)
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer then
            if string.lower(player.Name):match(lowerName) or string.lower(player.DisplayName):match(lowerName) then
                Features.TargetPlayer.SelectedTarget = player.Name
                getgenv().SelectedTarget = player.Name
                notify('Target Player', 'Locked onto: ' .. player.Name, 3)
                return
            end
        end
    end
    
    Features.TargetPlayer.SelectedTarget = nil
    getgenv().SelectedTarget = nil
    notify('Target Player', 'Player not found', 3)
end

function Features.TargetPlayer.getTargetPlayer()
    if not Features.TargetPlayer.Enabled or not Features.TargetPlayer.SelectedTarget then return nil end
    return Players:FindFirstChild(Features.TargetPlayer.SelectedTarget)
end

Players.PlayerAdded:Connect(function() task.wait(0.5); Features.TargetPlayer.updatePlayerList() end)
Players.PlayerRemoving:Connect(function(player) 
    if Features.TargetPlayer.SelectedTarget == player.Name then
        Features.TargetPlayer.SelectedTarget = nil
    end
    task.wait(0.5); Features.TargetPlayer.updatePlayerList() 
end)

Features.TargetPlayer.updatePlayerList()

Features.AvatarChanger = {
    Enabled = false,
    _persistentTasks = {}
}

local function descriptions_match(a, b)
    if not a or not b then return false end
    local keys = {"Shirt", "Pants", "ShirtGraphic", "Head", "Face", "BodyTypeScale", "HeightScale"}
    for _, k in ipairs(keys) do
        if (a[k] ~= nil and b[k] ~= nil) and tostring(a[k]) ~= tostring(b[k]) then return false end
    end
    return true
end

local function force_apply_brutal(hum, desc)
    if not hum or not desc then return false end
    for _ = 1, 15 do
        pcall(function() hum:ApplyDescriptionClientServer(desc) end)
        task.wait(0.05)
        local applied = pcall(function() return hum:GetAppliedDescription() end)
        if applied and descriptions_match(applied, desc) then return true end
    end
    
    pcall(function() hum.Description = Instance.new("HumanoidDescription") end)
    task.wait(0.1)
    
    for _ = 1, 15 do
        pcall(function() hum:ApplyDescriptionClientServer(desc) end)
        task.wait(0.05)
        local applied = pcall(function() return hum:GetAppliedDescription() end)
        if applied and descriptions_match(applied, desc) then return true end
    end
    return false
end

function Features.AvatarChanger.setAvatar(targetName)
    local char = LocalPlayer.Character
    local hum = char and char:FindFirstChildOfClass("Humanoid")
    if not hum or targetName == "" then return end

    local success, desc = pcall(function()
        local id = Players:GetUserIdFromNameAsync(targetName)
        return Players:GetHumanoidDescriptionFromUserId(id)
    end)

    if success and desc then
        pcall(function()
            LocalPlayer:ClearCharacterAppearance()
            hum.Description = Instance.new("HumanoidDescription")
        end)
        task.wait(0.05)
        force_apply_brutal(hum, desc)
        task.wait(0.1)
        pcall(function()
            if LocalPlayer.Character then
                LocalPlayer.Character:BreakJoints()
            end
        end)
    end
end

Features.Visuals = {
    Rain = {
        Enabled = false,
        Particles = {},
        MaxParticles = 5000,
        SpawnArea = 500,
        FallSpeed = 25,
        SpawnHeight = 100,
        SpawnRate = 3,
        Color = Color3.fromRGB(100, 200, 255)
    },
    Plasma = {
        Enabled = false,
        Active = false,
        NumTrails = 8,
        TrailColor = Color3.fromRGB(0, 255, 255),
        Attachments = {},
        LastBall = nil
    }
}

local function get_ball()
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return nil end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            return ball
        end
    end
    return nil
end

local ParticleFolder = Workspace:FindFirstChild("MagicalParticles") or Instance.new("Folder", Workspace)
ParticleFolder.Name = "MagicalParticles"

local function spawn_rain()
    local r = Features.Visuals.Rain
    if not r.Enabled or #r.Particles >= r.MaxParticles then return end
    
    local char = LocalPlayer.Character
    local pos = char and char.PrimaryPart and char.PrimaryPart.Position or Camera.CFrame.Position
    
    for _ = 1, r.SpawnRate do
        local particle = Instance.new("Part")
        particle.Size = Vector3.new(0.9, 0.9, 0.9)
        particle.Shape = Enum.PartType.Ball
        particle.Material = Enum.Material.Neon
        particle.Color = r.Color
        particle.CanCollide = false
        particle.Anchored = true
        particle.Position = Vector3.new(
            pos.X + math.random(-r.SpawnArea, r.SpawnArea),
            pos.Y + r.SpawnHeight,
            pos.Z + math.random(-r.SpawnArea, r.SpawnArea)
        )
        particle.Parent = ParticleFolder
        
        table.insert(r.Particles, {
            Part = particle,
            Velocity = Vector3.new(math.random(-2, 2), -r.FallSpeed, math.random(-2, 2)),
            TimeAlive = 0,
            FloatFreq = math.random(2, 4),
            FloatAmp = math.random(2, 5)
        })
    end
end

local function update_rain(delta)
    local r = Features.Visuals.Rain
    local char = LocalPlayer.Character
    local pos = char and char.PrimaryPart and char.PrimaryPart.Position or Camera.CFrame.Position
    
    for i = #r.Particles, 1, -1 do
        local p = r.Particles[i]
        if not p.Part or not p.Part.Parent then
            table.remove(r.Particles, i)
        else
            p.TimeAlive = p.TimeAlive + delta
            local float_x = math.sin(p.TimeAlive * p.FloatFreq) * p.FloatAmp * delta
            local float_z = math.cos(p.TimeAlive * p.FloatFreq) * p.FloatAmp * delta
            
            local new_pos = p.Part.Position + Vector3.new(
                p.Velocity.X * delta + float_x,
                p.Velocity.Y * delta,
                p.Velocity.Z * delta + float_z
            )
            p.Part.Position = new_pos
            
            if new_pos.Y < pos.Y - 20 or (new_pos - pos).Magnitude > r.SpawnArea * 1.5 then
                p.Part:Destroy()
                table.remove(r.Particles, i)
            end
        end
    end
end

local function create_plasma(ball)
    local p = Features.Visuals.Plasma
    if p.Active then return end
    p.Active = true
    p.Attachments = {}
    
    for i = 1, p.NumTrails do
        local angle = (i / p.NumTrails) * math.pi * 2
        local radius = math.random(150, 250) / 100
        local height = math.random(-150, 150) / 100
        
        local a0 = Instance.new("Attachment", ball)
        local a1 = Instance.new("Attachment", ball)
        
        local trail = Instance.new("Trail", ball)
        trail.Attachment0 = a0
        trail.Attachment1 = a1
        trail.Lifetime = 0.6
        trail.FaceCamera = true
        trail.LightEmission = 1
        trail.Color = ColorSequence.new(p.TrailColor)
        
        table.insert(p.Attachments, {
            a0 = a0, a1 = a1, trail = trail,
            baseAngle = angle, angle = 0, speed = math.random(15, 30) / 10,
            spiralSpeed = math.random(25, 45) / 10, baseRadius = radius, baseHeight = height
        })
    end
end

local function animate_plasma(delta)
    local p = Features.Visuals.Plasma
    if not p.Active then return end
    local time = tick()
    
    for _, t in ipairs(p.Attachments) do
        t.angle = t.angle + t.speed * delta
        local spiral = t.angle * t.spiralSpeed
        
        t.a0.Position = Vector3.new(
            math.cos(t.baseAngle + t.angle) * t.baseRadius,
            t.baseHeight + math.sin((t.baseAngle + t.angle) * 3) * 0.8,
            math.sin(t.baseAngle + t.angle) * t.baseRadius
        )
        
        t.a1.Position = Vector3.new(
            math.cos(t.baseAngle + t.angle + math.pi) * t.baseRadius,
            -t.baseHeight + math.cos((t.baseAngle + t.angle) * 2.5) * 0.8,
            math.sin(t.baseAngle + t.angle + math.pi) * t.baseRadius
        )
    end
end

local function cleanup_plasma(ball)
    if not ball then return end
    for _, obj in pairs(ball:GetChildren()) do
        if obj:IsA("Trail") or (obj:IsA("Attachment") and not obj.Name:match("Attachment")) then
            obj:Destroy()
        end
    end
    Features.Visuals.Plasma.Active = false
    Features.Visuals.Plasma.Attachments = {}
end

RunService.Heartbeat:Connect(function(delta)
    if Features.Visuals.Rain.Enabled then
        spawn_rain()
    end
    update_rain(delta)
    
    local ball = get_ball()
    local p = Features.Visuals.Plasma
    
    if p.Enabled then
        if ball and ball ~= p.LastBall then
            if p.LastBall then cleanup_plasma(p.LastBall) end
            create_plasma(ball)
            p.LastBall = ball
        end
        if ball and p.Active then animate_plasma(delta) end
    else
        if p.LastBall then
            cleanup_plasma(p.LastBall)
            p.LastBall = nil
        end
    end
end)

do
--[[
    LYRA UI REDESIGN V2 — AURORA MOCKUP
    UI only: no game feature logic is included.

    Direction:
      • Compact icon sidebar that expands on hover
      • Clean glass / premium visual language
      • Responsive desktop/mobile scaling
      • Logo used in the brand and minimized reopen button
      • Existing theme palettes retained

    Run as a LocalScript or in an environment that can parent a ScreenGui.
]]

if not game:IsLoaded() then game.Loaded:Wait() end

local Players = game:GetService("Players")
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local GuiService = game:GetService("GuiService")

local LocalPlayer = Players.LocalPlayer
if not LocalPlayer then
    Players:GetPropertyChangedSignal("LocalPlayer"):Wait()
    LocalPlayer = Players.LocalPlayer
end

local LOGO_FALLBACK = "rbxassetid://108975487405628"
local LOGO = LOGO_FALLBACK

-- ===========================================================================
-- Asset cache: Lyra/Blade Ball/Assets/*
-- Writes logo bytes to a file on first run, then loads with getcustomasset.
-- ===========================================================================
local ASSET_ROOT = "Lyra/Blade Ball"
local ASSET_DIR = ASSET_ROOT .. "/Assets"
local LOGO_FILE = ASSET_DIR .. "/logo.png"

-- Embedded Lyra Hub logo (PNG, base64). Written to ASSETS on startup when possible.
local LOGO_PNG_BASE64 = "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"

local function EnsureAssetFolders()
    if typeof(makefolder) ~= "function" or typeof(isfolder) ~= "function" then return false end
    pcall(function()
        if not isfolder("Lyra") then makefolder("Lyra") end
        if not isfolder(ASSET_ROOT) then makefolder(ASSET_ROOT) end
        if not isfolder(ASSET_DIR) then makefolder(ASSET_DIR) end
        if not isfolder(ASSET_ROOT .. "/Configs") then makefolder(ASSET_ROOT .. "/Configs") end
    end)
    return true
end

local function Base64Decode(data)
    local b = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    data = tostring(data or ""):gsub("[^" .. b .. "=]", "")
    return (data:gsub(".", function(x)
        if x == "=" then return "" end
        local r, f = "", (b:find(x, 1, true) - 1)
        for i = 6, 1, -1 do
            r = r .. ((f % 2^i - f % 2^(i - 1) > 0 and "1") or "0")
        end
        return r
    end):gsub("%d%d%d?%d?%d?%d?%d?%d?", function(x)
        if #x ~= 8 then return "" end
        local c = 0
        for i = 1, 8 do
            c = c + (x:sub(i, i) == "1" and 2^(8 - i) or 0)
        end
        return string.char(c)
    end))
end

local function ResolveCustomAsset(path)
    local loaders = {
        function() return getcustomasset(path) end,
        function() return getsynasset(path) end,
        function() return getcustomasset and getcustomasset(path) end,
        function()
            if syn and syn.getcustomasset then return syn.getcustomasset(path) end
        end,
        function()
            if crypt and crypt.base64 and readfile then
                -- some executors accept data urls poorly; skip
            end
        end,
    }
    for _, loader in ipairs(loaders) do
        local ok, result = pcall(loader)
        if ok and type(result) == "string" and result ~= "" then
            return result
        end
    end
    return nil
end

local function SaveAndLoadLogo()
    EnsureAssetFolders()
    local decoded
    local okDecode, decodedOrErr = pcall(Base64Decode, LOGO_PNG_BASE64)
    if okDecode then decoded = decodedOrErr end
    if type(decoded) ~= "string" or #decoded < 32 then
        return LOGO_FALLBACK
    end
    if typeof(writefile) == "function" then
        pcall(writefile, LOGO_FILE, decoded)
    end
    if typeof(isfile) == "function" and isfile(LOGO_FILE) then
        local asset = ResolveCustomAsset(LOGO_FILE)
        if asset then return asset end
    end
    return LOGO_FALLBACK
end

pcall(function()
    LOGO = SaveAndLoadLogo()
end)
if type(LOGO) ~= "string" or LOGO == "" then
    LOGO = LOGO_FALLBACK
end
local ICONS = {
    Home = "rbxassetid://6034509993",
    QB = "rbxassetid://6034684949",
    Catching = "rbxassetid://6034227067",
    Player = "rbxassetid://103807548355126",
    Physics = "rbxassetid://11537490966",
    Visual = "rbxassetid://88610077537468",
    Defense = "rbxassetid://14939023862",
    Automatics = "rbxassetid://6034502844",
    Trolling = "rbxassetid://6031097225",
    Misc = "rbxassetid://129082556946713",
    Settings = "rbxassetid://6031280882",
    Search = "rbxassetid://6031154871",
    Close = "rbxassetid://6031094678",
    Minimize = "rbxassetid://6031091004",
    Chevron = "rbxassetid://6031090990",
}

local Themes = {
    ["Aurora Gray"] = {
        MainBG = Color3.fromRGB(225, 228, 234), SidebarBG = Color3.fromRGB(216, 220, 227),
        TopbarBG = Color3.fromRGB(234, 237, 242), SectionBG = Color3.fromRGB(239, 242, 247),
        SectionHeaderBG = Color3.fromRGB(245, 247, 250), ElementBG = Color3.fromRGB(246, 248, 251),
        ElementHoverBG = Color3.fromRGB(252, 253, 255), Accent = Color3.fromRGB(138, 104, 238),
        TextMain = Color3.fromRGB(40, 45, 58), TextSub = Color3.fromRGB(103, 110, 126),
        Border = Color3.fromRGB(186, 193, 207), ControlBG = Color3.fromRGB(228, 232, 239),
    },
    ["Minimal White"] = {
        MainBG = Color3.fromRGB(247, 248, 250), SidebarBG = Color3.fromRGB(238, 241, 245),
        TopbarBG = Color3.fromRGB(243, 245, 248), SectionBG = Color3.fromRGB(250, 251, 253),
        SectionHeaderBG = Color3.fromRGB(245, 247, 250), ElementBG = Color3.fromRGB(255, 255, 255),
        ElementHoverBG = Color3.fromRGB(244, 247, 252), Accent = Color3.fromRGB(124, 92, 255),
        TextMain = Color3.fromRGB(36, 42, 56), TextSub = Color3.fromRGB(106, 114, 130),
        Border = Color3.fromRGB(212, 218, 228), ControlBG = Color3.fromRGB(236, 240, 246),
    },
    ["Midnight Violet"] = {
        MainBG = Color3.fromRGB(223, 214, 242), SidebarBG = Color3.fromRGB(214, 204, 235),
        TopbarBG = Color3.fromRGB(231, 223, 248), SectionBG = Color3.fromRGB(238, 231, 252),
        SectionHeaderBG = Color3.fromRGB(244, 238, 255), ElementBG = Color3.fromRGB(247, 242, 255),
        ElementHoverBG = Color3.fromRGB(252, 248, 255), Accent = Color3.fromRGB(138, 94, 232),
        TextMain = Color3.fromRGB(49, 39, 82), TextSub = Color3.fromRGB(103, 87, 140),
        Border = Color3.fromRGB(188, 171, 223), ControlBG = Color3.fromRGB(226, 217, 242),
    },
    ["Tokyo Night"] = {
        MainBG = Color3.fromRGB(52, 59, 84), SidebarBG = Color3.fromRGB(46, 53, 77),
        TopbarBG = Color3.fromRGB(58, 65, 92), SectionBG = Color3.fromRGB(67, 75, 105),
        SectionHeaderBG = Color3.fromRGB(74, 82, 116), ElementBG = Color3.fromRGB(82, 90, 125),
        ElementHoverBG = Color3.fromRGB(95, 104, 142), Accent = Color3.fromRGB(122, 162, 247),
        TextMain = Color3.fromRGB(233, 238, 255), TextSub = Color3.fromRGB(191, 198, 226),
        Border = Color3.fromRGB(110, 121, 162), ControlBG = Color3.fromRGB(60, 68, 97),
    },
    Dracula = {
        MainBG = Color3.fromRGB(74, 76, 98), SidebarBG = Color3.fromRGB(66, 68, 88),
        TopbarBG = Color3.fromRGB(82, 84, 108), SectionBG = Color3.fromRGB(92, 94, 121),
        SectionHeaderBG = Color3.fromRGB(100, 102, 130), ElementBG = Color3.fromRGB(108, 110, 140),
        ElementHoverBG = Color3.fromRGB(122, 124, 156), Accent = Color3.fromRGB(189, 147, 249),
        TextMain = Color3.fromRGB(248, 248, 242), TextSub = Color3.fromRGB(220, 222, 233),
        Border = Color3.fromRGB(136, 139, 170), ControlBG = Color3.fromRGB(80, 82, 105),
    },
    Catppuccin = {
        MainBG = Color3.fromRGB(239, 241, 245), SidebarBG = Color3.fromRGB(230, 233, 239),
        TopbarBG = Color3.fromRGB(245, 247, 250), SectionBG = Color3.fromRGB(248, 250, 252),
        SectionHeaderBG = Color3.fromRGB(241, 244, 249), ElementBG = Color3.fromRGB(255, 255, 255),
        ElementHoverBG = Color3.fromRGB(245, 248, 252), Accent = Color3.fromRGB(136, 57, 239),
        TextMain = Color3.fromRGB(76, 79, 105), TextSub = Color3.fromRGB(108, 111, 133),
        Border = Color3.fromRGB(203, 208, 218), ControlBG = Color3.fromRGB(232, 235, 243),
    },
    ["Rose Pine"] = {
        MainBG = Color3.fromRGB(244, 238, 235), SidebarBG = Color3.fromRGB(236, 229, 225),
        TopbarBG = Color3.fromRGB(249, 244, 241), SectionBG = Color3.fromRGB(252, 248, 246),
        SectionHeaderBG = Color3.fromRGB(244, 238, 235), ElementBG = Color3.fromRGB(255, 251, 249),
        ElementHoverBG = Color3.fromRGB(248, 242, 239), Accent = Color3.fromRGB(170, 122, 171),
        TextMain = Color3.fromRGB(83, 68, 90), TextSub = Color3.fromRGB(122, 106, 126),
        Border = Color3.fromRGB(216, 201, 196), ControlBG = Color3.fromRGB(239, 232, 228),
    },
    ["Hello Kitty"] = {
        MainBG = Color3.fromRGB(255, 231, 239), SidebarBG = Color3.fromRGB(255, 218, 230),
        TopbarBG = Color3.fromRGB(255, 224, 236), SectionBG = Color3.fromRGB(255, 240, 246),
        SectionHeaderBG = Color3.fromRGB(255, 226, 238), ElementBG = Color3.fromRGB(255, 248, 251),
        ElementHoverBG = Color3.fromRGB(255, 229, 240), Accent = Color3.fromRGB(235, 70, 105),
        TextMain = Color3.fromRGB(88, 36, 53), TextSub = Color3.fromRGB(154, 84, 105),
        Border = Color3.fromRGB(245, 156, 181), ControlBG = Color3.fromRGB(255, 236, 244),
    },
}

-- Extra colors only extend the original palettes; none of the original colors are replaced.
local ThemeAccents = {
    ["Aurora Gray"] = {Accent2 = Color3.fromRGB(201, 136, 255), Accent3 = Color3.fromRGB(111, 156, 255)},
    ["Minimal White"] = {Accent2 = Color3.fromRGB(255, 138, 188), Accent3 = Color3.fromRGB(120, 164, 255)},
    ["Midnight Violet"] = {Accent2 = Color3.fromRGB(226, 132, 214), Accent3 = Color3.fromRGB(126, 143, 245)},
    ["Tokyo Night"] = {Accent2 = Color3.fromRGB(125, 207, 255), Accent3 = Color3.fromRGB(187, 154, 247)},
    Dracula = {Accent2 = Color3.fromRGB(255, 121, 198), Accent3 = Color3.fromRGB(139, 233, 253)},
    Catppuccin = {Accent2 = Color3.fromRGB(244, 184, 228), Accent3 = Color3.fromRGB(114, 135, 253)},
    ["Rose Pine"] = {Accent2 = Color3.fromRGB(230, 170, 112), Accent3 = Color3.fromRGB(144, 122, 169)},
    ["Hello Kitty"] = {Accent2 = Color3.fromRGB(255, 148, 188), Accent3 = Color3.fromRGB(196, 102, 255)},
}

for name, extra in pairs(ThemeAccents) do
    Themes[name].Accent2 = extra.Accent2
    Themes[name].Accent3 = extra.Accent3
end

local themeName = "Midnight Violet"
local T = Themes[themeName]
local themeBindings = {}
local connections = {}
local destroyed = false
local uiScaleFactor = 0.84
local notificationsEnabled = true
local sidebarHoverEnabled = true
local menuToggleKey = Enum.KeyCode.RightControl
local GetTargetScale
local SetSidebar
local homeThemeStat, sessionTheme, runtimeTheme  -- shared with Settings theme dropdown

local function DetectExecutor()
    local probes = {
        function() if identifyexecutor then return identifyexecutor() end end,
        function() if getexecutorname then return getexecutorname() end end,
        function() if getexecutor then return getexecutor() end end,
        function() if syn and syn.getexecutor then return syn.getexecutor() end end,
    }
    for _, probe in ipairs(probes) do
        local ok, value = pcall(probe)
        if ok and value and tostring(value) ~= "" then
            return tostring(value)
        end
    end
    return "Unknown"
end

local executorName = DetectExecutor()

local function New(className, props)
    local object = Instance.new(className)
    for property, value in pairs(props or {}) do
        if property ~= "Parent" then object[property] = value end
    end
    if props and props.Parent then object.Parent = props.Parent end
    return object
end

local function Corner(parent, radius)
    return New("UICorner", {Parent = parent, CornerRadius = UDim.new(0, radius or 10)})
end

local function Stroke(parent, colorKey, transparency, thickness)
    local stroke = New("UIStroke", {
        Parent = parent, Color = T[colorKey or "Border"],
        Transparency = transparency or 0, Thickness = thickness or 1,
        ApplyStrokeMode = Enum.ApplyStrokeMode.Border,
    })
    table.insert(themeBindings, function()
        if stroke.Parent then stroke.Color = T[colorKey or "Border"] end
    end)
    return stroke
end

local function BindTheme(callback)
    table.insert(themeBindings, callback)
    callback()
end



local function Mix(a, b, alpha)
    return a:Lerp(b, alpha)
end

local function Gradient(parent, keys, rotation, transparency)
    local gradient = New("UIGradient", {
        Parent = parent,
        Rotation = rotation or 0,
        Color = ColorSequence.new(Color3.new(1, 1, 1)),
        Transparency = transparency or NumberSequence.new(0),
    })
    BindTheme(function()
        if not gradient.Parent then return end
        local points = {}
        for index, key in ipairs(keys) do
            local position = (#keys == 1) and 0 or ((index - 1) / (#keys - 1))
            local color
            if type(key) == "string" then color = T[key]
            else color = key(T) end
            points[index] = ColorSequenceKeypoint.new(position, color)
        end
        gradient.Color = ColorSequence.new(points)
    end)
    return gradient
end

local function Tween(object, duration, properties, style, direction)
    if not object or not object.Parent then return end
    local tween = TweenService:Create(object, TweenInfo.new(
        duration or 0.2, style or Enum.EasingStyle.Quart,
        direction or Enum.EasingDirection.Out
    ), properties)
    tween:Play()
    return tween
end

local parent
pcall(function()
    parent = (gethui and gethui()) or game:GetService("CoreGui")
end)
if not parent then parent = LocalPlayer:WaitForChild("PlayerGui") end

for _, location in ipairs({parent, LocalPlayer:FindFirstChild("PlayerGui")}) do
    if location then
        local old = location:FindFirstChild("LyraBladeBall")
        if old then old:Destroy() end
    end
end

local Screen = New("ScreenGui", {
    Parent = parent, Name = "LyraBladeBall", ResetOnSpawn = false,
    IgnoreGuiInset = false, ZIndexBehavior = Enum.ZIndexBehavior.Sibling,
})

local Dim = New("Frame", {
    Parent = Screen, Name = "Dim", Size = UDim2.fromScale(1, 1),
    BackgroundColor3 = T.MainBG, BackgroundTransparency = 1,
    BorderSizePixel = 0,
})

local Window = New("CanvasGroup", {
    Parent = Screen, Name = "Window", AnchorPoint = Vector2.new(0.5, 0.5),
    Position = UDim2.fromScale(0.5, 0.5), Size = UDim2.fromOffset(900, 540),
    BackgroundColor3 = T.MainBG, BorderSizePixel = 0, ClipsDescendants = true,
    GroupTransparency = 0, Active = true,
})
Corner(Window, 18)
local windowStroke = Stroke(Window, "Border", 0.12, 1.2)
local Scale = New("UIScale", {Parent = Window, Scale = uiScaleFactor})
Gradient(Window, {
    function(t) return Mix(t.MainBG, t.Accent3, 0.10) end,
    "MainBG",
    function(t) return Mix(t.MainBG, t.Accent2, 0.11) end,
}, 22)

-- Theme-colored aurora fields. They overlap to mix all three accent colors.
local AuroraLayer = New("Frame", {
    Parent = Window, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
    ClipsDescendants = true, ZIndex = 0,
})
local function AuroraBlob(position, size, colorKey, rotation)
    local blob = New("Frame", {
        Parent = AuroraLayer, AnchorPoint = Vector2.new(0.5, 0.5), Position = position,
        Size = size, BackgroundColor3 = T[colorKey], BackgroundTransparency = 0.88,
        BorderSizePixel = 0, ZIndex = 0,
    })
    Corner(blob, 999)
    local fade = New("UIGradient", {
        Parent = blob, Rotation = rotation or 0,
        Transparency = NumberSequence.new({
            NumberSequenceKeypoint.new(0, 0.28),
            NumberSequenceKeypoint.new(0.48, 0.63),
            NumberSequenceKeypoint.new(1, 1),
        }),
    })
    BindTheme(function()
        if blob.Parent then blob.BackgroundColor3 = T[colorKey] end
    end)
    return blob
end
local AuroraA = AuroraBlob(UDim2.fromScale(0.13, 0.03), UDim2.fromOffset(500, 330), "Accent", 28)
local AuroraB = AuroraBlob(UDim2.fromScale(0.88, 0.12), UDim2.fromOffset(470, 300), "Accent2", 145)
local AuroraC = AuroraBlob(UDim2.fromScale(0.70, 0.96), UDim2.fromOffset(610, 330), "Accent3", -20)

-- Ambient premium glow, clipped inside the window.
local GlowOne = New("Frame", {
    Parent = Window, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(0.17, 0.06),
    Size = UDim2.fromOffset(420, 260), BackgroundColor3 = T.Accent,
    BackgroundTransparency = 0.95, BorderSizePixel = 0, ZIndex = 0,
})
Corner(GlowOne, 200)
local glowGradient = New("UIGradient", {
    Parent = GlowOne,
    Transparency = NumberSequence.new({
        NumberSequenceKeypoint.new(0, 0.15), NumberSequenceKeypoint.new(1, 1),
    }), Rotation = 35,
})

local SIDEBAR_COMPACT, SIDEBAR_OPEN = 64, 188
local sidebarExpanded = false
local currentPage = "Home"
local navEntries = {}
local pages = {}

local Sidebar = New("Frame", {
    Parent = Window, Name = "Sidebar", Size = UDim2.new(0, SIDEBAR_COMPACT, 1, 0),
    BackgroundColor3 = T.SidebarBG, BackgroundTransparency = 0.08,
    BorderSizePixel = 0, ZIndex = 20, ClipsDescendants = true,
})
Gradient(Sidebar, {
    function(t) return Mix(t.SidebarBG, t.Accent, 0.16) end,
    "SidebarBG",
    function(t) return Mix(t.SidebarBG, t.Accent3, 0.13) end,
}, 90)
local sideLine = New("Frame", {
    Parent = Sidebar, AnchorPoint = Vector2.new(1, 0), Position = UDim2.fromScale(1, 0),
    Size = UDim2.new(0, 1, 1, 0), BackgroundColor3 = T.Border,
    BackgroundTransparency = 0.35, BorderSizePixel = 0, ZIndex = 21,
})

local Brand = New("Frame", {
    Parent = Sidebar, Size = UDim2.new(1, 0, 0, 78), BackgroundTransparency = 1, ZIndex = 22,
})
local BrandLogoShell = New("Frame", {
    Parent = Brand, Position = UDim2.fromOffset(17, 17), Size = UDim2.fromOffset(42, 42),
    BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.05, ZIndex = 23,
    ClipsDescendants = true,
})
-- Full circle (not rounded square)
New("UICorner", {Parent = BrandLogoShell, CornerRadius = UDim.new(1, 0)})
Gradient(BrandLogoShell, {"Accent", "Accent2", "Accent3"}, 35)
Stroke(BrandLogoShell, "Accent2", 0.08, 1.2)
local BrandLogo = New("ImageLabel", {
    Parent = BrandLogoShell, AnchorPoint = Vector2.new(0.5, 0.5),
    Position = UDim2.fromScale(0.5, 0.5), Size = UDim2.fromScale(1, 1),
    BackgroundTransparency = 1, Image = LOGO, ScaleType = Enum.ScaleType.Crop, ZIndex = 24,
})
New("UICorner", {Parent = BrandLogo, CornerRadius = UDim.new(1, 0)})
-- Late-bind custom logo if executor asset APIs become ready a moment later
task.spawn(function()
    task.wait(0.25)
    local asset = nil
    pcall(function()
        if typeof(isfile) == "function" and isfile(LOGO_FILE) then
            asset = ResolveCustomAsset(LOGO_FILE)
        end
    end)
    if type(asset) == "string" and asset ~= "" then
        LOGO = asset
        if BrandLogo and BrandLogo.Parent then BrandLogo.Image = LOGO end
        if OpenButton and OpenButton.Parent and OpenButton:IsA("ImageButton") then
            OpenButton.Image = LOGO
        end
    end
end)
local BrandTitle = New("TextLabel", {
    Parent = Brand, Position = UDim2.fromOffset(72, 18), Size = UDim2.new(1, -84, 0, 22),
    BackgroundTransparency = 1, Text = "LYRA HUB", Font = Enum.Font.GothamBold,
    TextSize = 16, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 23,
})
local BrandSub = New("TextLabel", {
    Parent = Brand, Position = UDim2.fromOffset(72, 40), Size = UDim2.new(1, -84, 0, 16),
    BackgroundTransparency = 1, Text = "BLADE BALL", Font = Enum.Font.GothamMedium,
    TextSize = 8, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 23,
})

local NavScroll = New("ScrollingFrame", {
    Parent = Sidebar, Position = UDim2.fromOffset(0, 78), Size = UDim2.new(1, 0, 1, -150),
    BackgroundTransparency = 1, BorderSizePixel = 0, ScrollBarThickness = 0,
    CanvasSize = UDim2.new(), AutomaticCanvasSize = Enum.AutomaticSize.Y, ZIndex = 22,
})
New("UIPadding", {
    Parent = NavScroll, PaddingLeft = UDim.new(0, 10), PaddingRight = UDim.new(0, 10),
    PaddingTop = UDim.new(0, 4), PaddingBottom = UDim.new(0, 8),
})
New("UIListLayout", {Parent = NavScroll, Padding = UDim.new(0, 5), SortOrder = Enum.SortOrder.LayoutOrder})

local SideFooter = New("Frame", {
    Parent = Sidebar, AnchorPoint = Vector2.new(0, 1), Position = UDim2.fromScale(0, 1),
    Size = UDim2.new(1, 0, 0, 70), BackgroundTransparency = 1, ZIndex = 22,
})
local Profile = New("Frame", {
    Parent = SideFooter, Position = UDim2.fromOffset(10, 8), Size = UDim2.new(1, -20, 0, 50),
    BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.25, ZIndex = 23,
})
Corner(Profile, 12)
Gradient(Profile, {function(t) return Mix(t.ElementBG, t.Accent3, .13) end, function(t) return Mix(t.ElementBG, t.Accent2, .10) end}, 15)
local Avatar = New("ImageLabel", {
    Parent = Profile, Position = UDim2.fromOffset(8, 8), Size = UDim2.fromOffset(34, 34),
    BackgroundColor3 = T.ControlBG, Image = "", ZIndex = 24,
})
Corner(Avatar, 10)
local ProfileName = New("TextLabel", {
    Parent = Profile, Position = UDim2.fromOffset(50, 7), Size = UDim2.new(1, -56, 0, 19),
    BackgroundTransparency = 1, Text = LocalPlayer.DisplayName, Font = Enum.Font.GothamSemibold,
    TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 24,
})
local ProfileStatus = New("TextLabel", {
    Parent = Profile, Position = UDim2.fromOffset(50, 25), Size = UDim2.new(1, -56, 0, 16),
    BackgroundTransparency = 1, Text = "●  BLADE BALL", Font = Enum.Font.GothamMedium,
    TextSize = 8, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, ZIndex = 24,
})
task.spawn(function()
    local ok, image = pcall(function()
        return Players:GetUserThumbnailAsync(LocalPlayer.UserId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size100x100)
    end)
    if ok and Avatar.Parent then Avatar.Image = image end
end)

local CONTENT_RIGHT_INSET = 8
local Content = New("Frame", {
    Parent = Window, Name = "Content", Position = UDim2.fromOffset(SIDEBAR_COMPACT, 0),
    Size = UDim2.new(1, -(SIDEBAR_COMPACT + CONTENT_RIGHT_INSET), 1, 0), BackgroundTransparency = 1, ZIndex = 5,
    Active = true,
})

local function ApplyContentLayout(expanded, instant)
    local offset = expanded and SIDEBAR_OPEN or SIDEBAR_COMPACT
    if instant then
        Content.Position = UDim2.fromOffset(offset, 0)
        Content.Size = UDim2.new(1, -(offset + CONTENT_RIGHT_INSET), 1, 0)
    else
        Tween(Content, 0.28, {
            Position = UDim2.fromOffset(offset, 0),
            Size = UDim2.new(1, -(offset + CONTENT_RIGHT_INSET), 1, 0),
        })
    end
end

local Topbar = New("Frame", {
    Parent = Content, Size = UDim2.new(1, 0, 0, 72), BackgroundColor3 = T.TopbarBG,
    BackgroundTransparency = 0.18, BorderSizePixel = 0, ZIndex = 10,
    Active = true,
})
Gradient(Topbar, {
    function(t) return Mix(t.TopbarBG, t.Accent3, 0.16) end,
    "TopbarBG",
    function(t) return Mix(t.TopbarBG, t.Accent2, 0.14) end,
}, 0)
local topLine = New("Frame", {
    Parent = Topbar, AnchorPoint = Vector2.new(0, 1), Position = UDim2.fromScale(0, 1),
    Size = UDim2.new(1, 0, 0, 1), BackgroundColor3 = T.Border,
    BackgroundTransparency = 0.45, BorderSizePixel = 0, ZIndex = 11,
})
local PageTitle = New("TextLabel", {
    Parent = Topbar, Position = UDim2.fromOffset(24, 14), Size = UDim2.fromOffset(280, 24),
    BackgroundTransparency = 1, Text = "Status", Font = Enum.Font.GothamBold,
    TextSize = 19, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 12,
})
local Breadcrumb = New("TextLabel", {
    Parent = Topbar, Position = UDim2.fromOffset(24, 39), Size = UDim2.fromOffset(360, 16),
    BackgroundTransparency = 1, Text = "Lyra  /  Status", Font = Enum.Font.Gotham,
    TextSize = 10, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 12,
})

local SearchBox = New("Frame", {
    Parent = Topbar, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -104, 0.5, 0),
    Size = UDim2.fromOffset(210, 36), BackgroundColor3 = T.ControlBG,
    BackgroundTransparency = 0.12, ZIndex = 12,
})
Corner(SearchBox, 11)
Gradient(SearchBox, {function(t) return Mix(t.ControlBG, t.Accent3, .12) end, function(t) return Mix(t.ControlBG, t.Accent2, .10) end}, 0)
Stroke(SearchBox, "Border", 0.2)
local SearchIcon = New("ImageLabel", {
    Parent = SearchBox, Position = UDim2.fromOffset(11, 9), Size = UDim2.fromOffset(18, 18),
    BackgroundTransparency = 1, Image = ICONS.Search, ImageColor3 = T.TextSub, ZIndex = 13,
})
local SearchInput = New("TextBox", {
    Parent = SearchBox, Position = UDim2.fromOffset(38, 0), Size = UDim2.new(1, -46, 1, 0),
    BackgroundTransparency = 1, Text = "", PlaceholderText = "Search this page...",
    PlaceholderColor3 = T.TextSub, TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
    TextSize = 11, TextXAlignment = Enum.TextXAlignment.Left, ClearTextOnFocus = false, ZIndex = 13,
})

local function IconButton(icon, xOffset)
    local button = New("ImageButton", {
        Parent = Topbar, AnchorPoint = Vector2.new(1, 0.5),
        Position = UDim2.new(1, xOffset, 0.5, 0), Size = UDim2.fromOffset(32, 32),
        BackgroundColor3 = T.ControlBG, BackgroundTransparency = 0.18,
        Image = icon, ImageColor3 = T.TextSub, AutoButtonColor = false, ZIndex = 12,
    })
    Corner(button, 10)
    button.MouseEnter:Connect(function()
        Tween(button, 0.16, {BackgroundTransparency = 0, ImageColor3 = T.TextMain})
    end)
    button.MouseLeave:Connect(function()
        Tween(button, 0.16, {BackgroundTransparency = 0.18, ImageColor3 = T.TextSub})
    end)
    return button
end
local MinimizeButton = IconButton(ICONS.Minimize, -58)
local CloseButton = IconButton(ICONS.Close, -18)

local PageHost = New("Frame", {
    Parent = Content, Position = UDim2.fromOffset(0, 72), Size = UDim2.new(1, 0, 1, -72),
    BackgroundTransparency = 1, ClipsDescendants = true, ZIndex = 6,
})

local OpenButton = New("ImageButton", {
    Parent = Screen, Name = "OpenButton", AnchorPoint = Vector2.new(0, 0.5),
    Position = UDim2.new(0, 20, 0.5, 0), Size = UDim2.fromOffset(54, 54),
    BackgroundColor3 = T.MainBG, BackgroundTransparency = 0.04,
    Image = LOGO, ScaleType = Enum.ScaleType.Crop, Visible = false, ClipsDescendants = true,
    AutoButtonColor = false, ZIndex = 100,
})
New("UICorner", {Parent = OpenButton, CornerRadius = UDim.new(1, 0)})
Gradient(OpenButton, {"Accent", "Accent2", "Accent3"}, 35)
Stroke(OpenButton, "Accent2", 0.02, 1.6)
New("UIPadding", {
    Parent = OpenButton, PaddingTop = UDim.new(0, 8), PaddingBottom = UDim.new(0, 8),
    PaddingLeft = UDim.new(0, 8), PaddingRight = UDim.new(0, 8),
})

local DropdownLayer = New("Frame", {
    Parent = Window, Name = "DropdownLayer", Size = UDim2.fromScale(1, 1),
    BackgroundTransparency = 1, BorderSizePixel = 0, ZIndex = 350, Visible = false,
})

-- Notification preview -------------------------------------------------------
local ToastHost = New("Frame", {
    Parent = Screen, AnchorPoint = Vector2.new(1, 1), Position = UDim2.new(1, -18, 1, -18),
    Size = UDim2.fromOffset(300, 300), BackgroundTransparency = 1, ZIndex = 200,
})
New("UIListLayout", {
    Parent = ToastHost, VerticalAlignment = Enum.VerticalAlignment.Bottom,
    HorizontalAlignment = Enum.HorizontalAlignment.Right, Padding = UDim.new(0, 8),
})
local function Toast(title, message, duration)
    local holder = New("Frame", {
        Parent = ToastHost, Size = UDim2.fromOffset(280, 0), BackgroundTransparency = 1,
        ClipsDescendants = true, ZIndex = 201,
    })
    local card = New("CanvasGroup", {
        Parent = holder, Position = UDim2.fromOffset(300, 0), Size = UDim2.fromOffset(280, 76),
        BackgroundColor3 = T.SectionBG, BackgroundTransparency = 0.06,
        GroupTransparency = 1, ZIndex = 202,
    })
    Corner(card, 13)
    Stroke(card, "Border", 0.16)
    local bar = New("Frame", {
        Parent = card, Size = UDim2.fromOffset(3, 76), BackgroundColor3 = T.Accent,
        BorderSizePixel = 0, ZIndex = 203,
    })
    Corner(bar, 3)
    New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(16, 12), Size = UDim2.new(1, -28, 0, 18),
        BackgroundTransparency = 1, Text = title, Font = Enum.Font.GothamBold,
        TextSize = 12, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 203,
    })
    New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(16, 33), Size = UDim2.new(1, -28, 0, 32),
        BackgroundTransparency = 1, Text = message, Font = Enum.Font.Gotham,
        TextSize = 10, TextColor3 = T.TextSub, TextWrapped = true,
        TextXAlignment = Enum.TextXAlignment.Left, TextYAlignment = Enum.TextYAlignment.Top, ZIndex = 203,
    })
    Tween(holder, 0.22, {Size = UDim2.fromOffset(280, 76)})
    Tween(card, 0.35, {Position = UDim2.fromOffset(0, 0), GroupTransparency = 0})
    task.delay(duration or 3.2, function()
        if not card.Parent then return end
        local out = Tween(card, 0.28, {Position = UDim2.fromOffset(300, 0), GroupTransparency = 1})
        if out then out.Completed:Wait() end
        if holder.Parent then holder:Destroy() end
    end)
end

notify = function(title, message, duration)
    if notificationsEnabled then
        Toast(title, message, duration)
    end
end

-- UI component factory ------------------------------------------------------

-- ============================================================================
-- Lyra ConfigManager (per-script root)
-- Root is derived only from SCRIPT_ID — never shared across scripts.
--   Lyra/<SCRIPT_ID>/Configs/*.json
--   Lyra/<SCRIPT_ID>/Autoload.txt
-- ============================================================================
local SCRIPT_ID = "Blade Ball"
local ConfigManager = (function()
    local HttpService = game:GetService("HttpService")
    local CM = {
        ScriptId = SCRIPT_ID,
        Root = "Lyra/" .. SCRIPT_ID,
        Folder = "Lyra/" .. SCRIPT_ID .. "/Configs",
        AutoloadFile = "Lyra/" .. SCRIPT_ID .. "/Autoload.txt",
        Flags = {},
        Ignore = { ["Config Name"] = true, ["View Configs"] = true, ["Autoload"] = true },
    }

    local function ensureTree()
        if typeof(makefolder) ~= "function" or typeof(isfolder) ~= "function" then return end
        if not isfolder("Lyra") then pcall(makefolder, "Lyra") end
        if not isfolder(CM.Root) then pcall(makefolder, CM.Root) end
        if not isfolder(CM.Folder) then pcall(makefolder, CM.Folder) end
    end

    local function sanitize(name)
        name = tostring(name or ""):gsub("^%s+", ""):gsub("%s+$", "")
        name = name:gsub("[\\/:*?\"<>|]", "_")
        return (name ~= "" and name) or "default"
    end

    local function encode(value)
        local t = typeof(value)
        if t == "Color3" then
            return { Type = "Color3", R = value.R, G = value.G, B = value.B }
        elseif t == "EnumItem" then
            return { Type = "EnumItem", EnumType = tostring(value.EnumType), Name = value.Name }
        elseif t == "table" then
            local out = {}
            for k, v in pairs(value) do out[k] = encode(v) end
            return out
        end
        return value
    end

    local function decode(value)
        if type(value) ~= "table" then return value end
        if value.Type == "Color3" then
            return Color3.new(value.R or 0, value.G or 0, value.B or 0)
        elseif value.Type == "EnumItem" then
            local et = Enum[value.EnumType]
            if et then
                local ok, item = pcall(function() return et[value.Name] end)
                if ok then return item end
            end
            return nil
        end
        local out = {}
        for k, v in pairs(value) do out[k] = decode(v) end
        return out
    end

    function CM.Register(name, control)
        if type(name) == "string" and type(control) == "table" then
            CM.Flags[name] = control
        end
        return control
    end

    function CM.List()
        local names = {}
        if typeof(listfiles) ~= "function" then return names end
        ensureTree()
        local ok, files = pcall(listfiles, CM.Folder)
        if not ok or type(files) ~= "table" then return names end
        for _, entry in ipairs(files) do
            local path = entry
            if type(entry) == "table" then
                path = entry.Path or entry.path or entry.Name or entry.name or tostring(entry)
            end
            if type(path) == "string" then
                local n = path:match("([^/\\]+)%.json$")
                if n then table.insert(names, n) end
            end
        end
        table.sort(names)
        return names
    end

    function CM.Save(name)
        if typeof(writefile) ~= "function" then return false, "no writefile" end
        ensureTree()
        name = sanitize(name)
        local data = {}
        for flagName, flag in pairs(CM.Flags) do
            if not CM.Ignore[flagName] and type(flag) == "table" and type(flag.Get) == "function" then
                local ok, value = pcall(flag.Get)
                if ok then data[flagName] = encode(value) end
            end
        end
        local ok, encoded = pcall(function() return HttpService:JSONEncode(data) end)
        if not ok then return false, encoded end
        local path = CM.Folder .. "/" .. name .. ".json"
        local wok, err = pcall(writefile, path, encoded)
        return wok, wok and path or err
    end

    function CM.Load(name)
        if typeof(readfile) ~= "function" or typeof(isfile) ~= "function" then
            return false, "no filesystem"
        end
        ensureTree()
        name = sanitize(name)
        local path = CM.Folder .. "/" .. name .. ".json"
        if not isfile(path) then return false, "not found" end
        local ok, raw = pcall(readfile, path)
        if not ok then return false, raw end
        local dok, data = pcall(function() return HttpService:JSONDecode(raw) end)
        if not dok or type(data) ~= "table" then return false, "bad json" end

        if data["UI Theme"] and CM.Flags["UI Theme"] then
            local decoded = decode(data["UI Theme"])
            pcall(function() CM.Flags["UI Theme"].Set(decoded, true) end)
        end

        local applied = 0
        for flagName, rawValue in pairs(data) do
            if not CM.Ignore[flagName] and flagName ~= "UI Theme" then
                local flag = CM.Flags[flagName]
                if flag and type(flag.Set) == "function" then
                    local value = decode(rawValue)
                    if pcall(function() flag.Set(value, true) end) then
                        applied = applied + 1
                    end
                end
            end
        end
        return true, applied
    end

    function CM.Delete(name)
        if typeof(delfile) ~= "function" or typeof(isfile) ~= "function" then
            return false, "no filesystem"
        end
        name = sanitize(name)
        local path = CM.Folder .. "/" .. name .. ".json"
        if isfile(path) then
            local ok, err = pcall(delfile, path)
            return ok, err
        end
        return false, "not found"
    end

    function CM.SetAutoload(name, enabled)
        if typeof(writefile) ~= "function" then return false end
        ensureTree()
        if enabled then
            pcall(writefile, CM.AutoloadFile, sanitize(name))
        elseif typeof(isfile) == "function" and isfile(CM.AutoloadFile) and typeof(delfile) == "function" then
            pcall(delfile, CM.AutoloadFile)
        end
        return true
    end

    function CM.GetAutoload()
        if typeof(readfile) ~= "function" or typeof(isfile) ~= "function" then return nil end
        if isfile(CM.AutoloadFile) then
            local ok, name = pcall(readfile, CM.AutoloadFile)
            if ok and type(name) == "string" and name ~= "" then
                return sanitize(name)
            end
        end
        return nil
    end

    function CM.LoadAutoload()
        local name = CM.GetAutoload()
        if not name then return false, "none" end
        return CM.Load(name)
    end

    ensureTree()
    return CM
end)()

local function CreatePage(name)
    local page = New("CanvasGroup", {
        Parent = PageHost, Name = name .. "Page", Size = UDim2.fromScale(1, 1),
        BackgroundTransparency = 1, Visible = false, GroupTransparency = 1,
    })
    local scroll = New("ScrollingFrame", {
        Parent = page, Size = UDim2.new(1, -10, 1, 0), BackgroundTransparency = 1,
        BorderSizePixel = 0, ScrollBarThickness = 6, ScrollBarImageColor3 = T.Accent,
        Active = true, ScrollingEnabled = true, ScrollingDirection = Enum.ScrollingDirection.Y,
        AutomaticCanvasSize = Enum.AutomaticSize.Y,
        -- CanvasSize is handled automatically via AutomaticCanvasSize
        CanvasSize = UDim2.fromOffset(0, 0),
        ScrollBarImageTransparency = 0.4,
    })
    New("UIPadding", {
        Parent = scroll, PaddingTop = UDim.new(0, 18), PaddingBottom = UDim.new(0, 24),
        PaddingLeft = UDim.new(0, 18), PaddingRight = UDim.new(0, 22),
    })
    -- UIListLayout goes DIRECTLY in the ScrollingFrame (no intermediate Frame wrapper)
    local bodyLayout = New("UIListLayout", {Parent = scroll, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})
    -- The ScrollingFrame itself IS the body. Sections are parented to scroll.
    -- AutomaticCanvasSize = Y will track content automatically.
    pages[name] = {Group = page, Scroll = scroll, Body = scroll, BodyLayout = bodyLayout, Searchables = {}}
    BindTheme(function()
        if scroll.Parent then scroll.ScrollBarImageColor3 = T.Accent end
    end)
    return pages[name]
end

local function Section(page, title, subtitle, widthScale)
    local card = New("Frame", {
        Parent = page.Body, Size = UDim2.new(widthScale or 1, 0, 0, 0),
        AutomaticSize = Enum.AutomaticSize.Y, BackgroundColor3 = T.SectionBG,
        BackgroundTransparency = 0.08,
    })
    Corner(card, 15)
    Gradient(card, {
        function(t) return Mix(t.SectionBG, t.Accent3, 0.10) end,
        "SectionBG",
        function(t) return Mix(t.SectionBG, t.Accent2, 0.09) end,
    }, 18)
    local cardStroke = Stroke(card, "Border", 0.16, 1.1)
    Gradient(cardStroke, {"Accent3", "Border", "Accent2"}, 0, NumberSequence.new({
        NumberSequenceKeypoint.new(0, .18), NumberSequenceKeypoint.new(.5, .58), NumberSequenceKeypoint.new(1, .18)
    }))
    New("UIPadding", {
        Parent = card, PaddingTop = UDim.new(0, 14), PaddingBottom = UDim.new(0, 14),
        PaddingLeft = UDim.new(0, 14), PaddingRight = UDim.new(0, 14),
    })
    New("UIListLayout", {Parent = card, Padding = UDim.new(0, 9), SortOrder = Enum.SortOrder.LayoutOrder})
    local heading = New("Frame", {
        Parent = card, Size = UDim2.new(1, 0, 0, subtitle and 42 or 25), BackgroundTransparency = 1,
    })
    local accent = New("Frame", {
        Parent = heading, Position = UDim2.fromOffset(0, 2), Size = UDim2.fromOffset(3, 19),
        BackgroundColor3 = T.Accent, BorderSizePixel = 0,
    })
    Corner(accent, 3)
    Gradient(accent, {"Accent", "Accent2"}, 90)
    local titleLabel = New("TextLabel", {
        Parent = heading, Position = UDim2.fromOffset(12, 0), Size = UDim2.new(1, -12, 0, 22),
        BackgroundTransparency = 1, Text = title, Font = Enum.Font.GothamSemibold,
        TextSize = 13, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local subLabel
    if subtitle then
        subLabel = New("TextLabel", {
            Parent = heading, Position = UDim2.fromOffset(12, 22), Size = UDim2.new(1, -12, 0, 17),
            BackgroundTransparency = 1, Text = subtitle, Font = Enum.Font.Gotham,
            TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        })
    end
    BindTheme(function()
        if card.Parent then
            card.BackgroundColor3 = T.SectionBG; accent.BackgroundColor3 = T.Accent
            titleLabel.TextColor3 = T.TextMain
            if subLabel then subLabel.TextColor3 = T.TextSub end
        end
    end)
    return card
end

local function ElementBase(page, parent, label, height)
    local row = New("Frame", {
        Parent = parent, Size = UDim2.new(1, 0, 0, height or 44),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06,
    })
    Corner(row, 11)
    Gradient(row, {
        function(t) return Mix(t.ElementBG, t.Accent3, .07) end,
        "ElementBG",
        function(t) return Mix(t.ElementBG, t.Accent2, .065) end,
    }, 0)
    local outline = Stroke(row, "Border", 0.32)
    table.insert(page.Searchables, {Object = row, Text = string.lower(label)})
    row.MouseEnter:Connect(function()
        Tween(row, 0.16, {BackgroundColor3 = T.ElementHoverBG, BackgroundTransparency = 0})
    end)
    row.MouseLeave:Connect(function()
        Tween(row, 0.16, {BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06})
    end)
    BindTheme(function()
        if row.Parent then row.BackgroundColor3 = T.ElementBG; outline.Color = T.Border end
    end)
    return row
end

local function Toggle(page, parentCard, label, default, onChanged)
    local state = default == true
    local row = ElementBase(page, parentCard, label, 44)
    local text = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(1, -72, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local track = New("Frame", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -12, 0.5, 0),
        Size = UDim2.fromOffset(38, 20), BackgroundColor3 = state and T.Accent or T.ControlBG,
    })
    Corner(track, 10)
    Gradient(track, {"Accent", "Accent2"}, 0)
    local knob = New("Frame", {
        Parent = track, AnchorPoint = Vector2.new(0, 0.5),
        Position = state and UDim2.new(1, -18, 0.5, 0) or UDim2.new(0, 3, 0.5, 0),
        Size = UDim2.fromOffset(15, 15), BackgroundColor3 = state and T.MainBG or T.TextSub,
    })
    Corner(knob, 8)
    local click = New("TextButton", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 4,
    })
    local function Paint(animate)
        local duration = animate and 0.18 or 0
        Tween(track, duration, {BackgroundColor3 = state and T.Accent or T.ControlBG})
        Tween(knob, duration, {
            Position = state and UDim2.new(1, -18, 0.5, 0) or UDim2.new(0, 3, 0.5, 0),
            BackgroundColor3 = state and T.MainBG or T.TextSub,
        })
    end
    local function SetState(value, animate, fire)
        state = value == true
        Paint(animate)
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, state) end)
        end
    end
    click.MouseButton1Click:Connect(function()
        SetState(not state, true, true)
    end)
    BindTheme(function()
        if row.Parent then text.TextColor3 = T.TextMain; Paint(false) end
    end)
    return {
        Get = function() return state end,
        Set = function(value, fire) SetState(value, true, fire == nil and true or fire) end,
    }
end

local function Slider(page, parentCard, label, min, max, value, suffix, onChanged)
    local row = ElementBase(page, parentCard, label, 62)
    local current = value or min
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 5), Size = UDim2.new(1, -88, 0, 22),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local valueText = New("TextLabel", {
        Parent = row, AnchorPoint = Vector2.new(1, 0), Position = UDim2.new(1, -12, 0, 6),
        Size = UDim2.fromOffset(66, 20), BackgroundTransparency = 1,
        Text = tostring(current) .. (suffix or ""), Font = Enum.Font.GothamSemibold,
        TextSize = 10, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Right,
    })
    local track = New("TextButton", {
        Parent = row, Position = UDim2.new(0, 13, 1, -19), Size = UDim2.new(1, -26, 0, 7),
        BackgroundColor3 = T.ControlBG, Text = "", AutoButtonColor = false,
    })
    Corner(track, 4)
    local fill = New("Frame", {
        Parent = track, Size = UDim2.new((current - min) / math.max(max - min, 0.0001), 0, 1, 0),
        BackgroundColor3 = T.Accent, BorderSizePixel = 0,
    })
    Corner(fill, 4)
    Gradient(fill, {"Accent3", "Accent", "Accent2"}, 0)
    local knob = New("Frame", {
        Parent = fill, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(1, 0.5),
        Size = UDim2.fromOffset(13, 13), BackgroundColor3 = T.TextMain,
    })
    Corner(knob, 7)
    Stroke(knob, "Accent", 0, 2)
    local dragging = false
    local function SetValue(newValue, fire)
        current = math.clamp(math.floor((newValue or min) * 10 + 0.5) / 10, min, max)
        local alpha = (current - min) / math.max(max - min, 0.0001)
        fill.Size = UDim2.new(alpha, 0, 1, 0)
        valueText.Text = tostring(current) .. (suffix or "")
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, current) end)
        end
    end
    local function Update(inputX)
        local alpha = math.clamp((inputX - track.AbsolutePosition.X) / track.AbsoluteSize.X, 0, 1)
        SetValue(min + ((max - min) * alpha), true)
    end
    track.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            dragging = true
            Update(input.Position.X)
        end
    end)
    table.insert(connections, UserInputService.InputChanged:Connect(function(input)
        if dragging and (input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch) then
            Update(input.Position.X)
        end
    end))
    table.insert(connections, UserInputService.InputEnded:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then dragging = false end
    end))
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            valueText.TextColor3 = T.Accent
            track.BackgroundColor3 = T.ControlBG
            fill.BackgroundColor3 = T.Accent
            knob.BackgroundColor3 = T.TextMain
        end
    end)
    return {
        Get = function() return current end,
        Set = function(v, fire) SetValue(v, fire == nil and true or fire) end,
    }
end

local activeDropdown
local activeColorPopup
local function Dropdown(page, parentCard, label, options, default, onChanged)
    local selected = default or options[1]
    local row = ElementBase(page, parentCard, label, 44)
    row.ClipsDescendants = false
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.48, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local box = New("TextButton", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(132, 28), BackgroundColor3 = T.ControlBG,
        Text = selected, TextColor3 = T.Accent, Font = Enum.Font.GothamMedium,
        TextSize = 10, AutoButtonColor = false, ZIndex = 10,
    })
    Corner(box, 9)
    Stroke(box, "Border", 0.35)

    local menuHeight = math.min(#options * 31 + 12, 205)
    local menu = New("ScrollingFrame", {
        Parent = DropdownLayer, Size = UDim2.fromOffset(170, menuHeight),
        BackgroundColor3 = T.SectionBG, Visible = false, ZIndex = 400,
        BorderSizePixel = 0, ScrollBarThickness = 3, ScrollBarImageColor3 = T.Accent,
        CanvasSize = UDim2.new(), AutomaticCanvasSize = Enum.AutomaticSize.Y,
        Active = true, ScrollingEnabled = true, ScrollingDirection = Enum.ScrollingDirection.Y,
    })
    Corner(menu, 12)
    Stroke(menu, "Border", 0.08)
    New("UIPadding", {
        Parent = menu, PaddingTop = UDim.new(0, 6), PaddingBottom = UDim.new(0, 6),
        PaddingLeft = UDim.new(0, 6), PaddingRight = UDim.new(0, 6),
    })
    New("UIListLayout", {Parent = menu, Padding = UDim.new(0, 3), SortOrder = Enum.SortOrder.LayoutOrder})

    local function PositionMenu()
        local windowPos = Window.AbsolutePosition
        local layerSize = DropdownLayer.AbsoluteSize
        local menuWidth = menu.AbsoluteSize.X > 0 and menu.AbsoluteSize.X or 170
        local finalHeight = menuHeight
        local x = (box.AbsolutePosition.X - windowPos.X) + box.AbsoluteSize.X - menuWidth
        local y = (box.AbsolutePosition.Y - windowPos.Y) + box.AbsoluteSize.Y + 6
        if y + finalHeight > layerSize.Y - 8 then
            y = (box.AbsolutePosition.Y - windowPos.Y) - finalHeight - 6
        end
        x = math.clamp(x, 8, math.max(8, layerSize.X - menuWidth - 8))
        y = math.clamp(y, 8, math.max(8, layerSize.Y - finalHeight - 8))
        menu.Position = UDim2.fromOffset(x, y)
    end

    for _, option in ipairs(options) do
        local item = New("TextButton", {
            Parent = menu, Size = UDim2.new(1, 0, 0, 28), BackgroundColor3 = T.ElementBG,
            BackgroundTransparency = 1, Text = option, TextColor3 = T.TextSub,
            Font = Enum.Font.Gotham, TextSize = 10, AutoButtonColor = false, ZIndex = 401,
        })
        Corner(item, 8)
        item.MouseEnter:Connect(function() Tween(item, 0.12, {BackgroundTransparency = 0, TextColor3 = T.TextMain}) end)
        item.MouseLeave:Connect(function() Tween(item, 0.12, {BackgroundTransparency = 1, TextColor3 = T.TextSub}) end)
        item.MouseButton1Click:Connect(function()
            selected = option; box.Text = option; menu.Visible = false; activeDropdown = nil; DropdownLayer.Visible = false
            if onChanged then onChanged(option) end
        end)
        BindTheme(function()
            if item.Parent then item.BackgroundColor3 = T.ElementBG; item.TextColor3 = T.TextSub end
        end)
    end
    box.MouseButton1Click:Connect(function()
        if activeDropdown and activeDropdown ~= menu then activeDropdown.Visible = false end
        if activeColorPopup then activeColorPopup.Visible = false; activeColorPopup = nil end
        PositionMenu()
        menu.Visible = not menu.Visible
        DropdownLayer.Visible = menu.Visible
        activeDropdown = menu.Visible and menu or nil
    end)
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain; box.BackgroundColor3 = T.ControlBG
            box.TextColor3 = T.Accent; menu.BackgroundColor3 = T.SectionBG
            menu.ScrollBarImageColor3 = T.Accent
        end
    end)
    local function SetSelected(value, fire)
        if value == nil then return end
        selected = value
        box.Text = tostring(value)
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, selected) end)
        end
    end
    return {
        Get = function() return selected end,
        Set = function(value, fire) SetSelected(value, fire == nil and true or fire) end,
        Refresh = function(newOptions)
            if type(newOptions) ~= "table" then return end
            options = newOptions
            for _, child in ipairs(menu:GetChildren()) do
                if child:IsA("TextButton") then child:Destroy() end
            end
            for _, option in ipairs(options) do
                local optionButton = New("TextButton", {
                    Parent = menu, Size = UDim2.new(1, -8, 0, 28), BackgroundColor3 = T.ElementBG,
                    Text = "  " .. tostring(option), TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
                    TextSize = 10, TextXAlignment = Enum.TextXAlignment.Left, AutoButtonColor = false, ZIndex = 60,
                })
                Corner(optionButton, 8)
                optionButton.MouseButton1Click:Connect(function()
                    SetSelected(option, true)
                    menu.Visible = false
                    DropdownLayer.Visible = false
                    activeDropdown = nil
                end)
            end
            menu.CanvasSize = UDim2.new(0, 0, 0, #options * 32)
        end,
    }
end

local function Button(page, parentCard, label, emphasized, action)
    -- FIXED v2: Complete rewrite to avoid ElementBase double-hover conflict
    -- Previously Button called ElementBase which added its own hover tween to ElementHoverBG,
    -- causing emphasized buttons (Accent bg + MainBG text) to become invisible on hover.
    -- This version creates its own row with proper contrast for ALL themes.
    local row = New("Frame", {
        Parent = parentCard, Size = UDim2.new(1, 0, 0, 42),
        BackgroundColor3 = emphasized and T.Accent or T.ElementBG,
        BackgroundTransparency = emphasized and 0.08 or 0.06,
    })
    Corner(row, 11)
    -- Only non-emphasized buttons keep subtle gradient, emphasized stays solid accent for readability
    if not emphasized then
        Gradient(row, {
            function(t) return Mix(t.ElementBG, t.Accent3, .07) end,
            "ElementBG",
            function(t) return Mix(t.ElementBG, t.Accent2, .065) end,
        }, 0)
    end
    local outline = Stroke(row, "Border", 0.32)
    table.insert(page.Searchables, {Object = row, Text = string.lower(label)})

    local function getAccentTextColor()
        local lum = 0.299 * T.Accent.R + 0.587 * T.Accent.G + 0.114 * T.Accent.B
        if lum > 0.55 then
            return Color3.fromRGB(28, 30, 38) -- dark text for light accents (Tokyo Night #7AA2F7, Dracula, Rose Pine)
        else
            return Color3.fromRGB(255, 255, 255) -- white for dark accents (Aurora Gray, Midnight Violet, etc.)
        end
    end
    local function getAccentHover()
        return T.Accent:Lerp(Color3.new(0, 0, 0), 0.18)
    end

    local text = New("TextLabel", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = label, Font = Enum.Font.GothamSemibold, TextSize = 11,
        TextColor3 = emphasized and getAccentTextColor() or T.TextMain,
    })
    local click = New("TextButton", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 4,
    })
    click.MouseButton1Down:Connect(function() Tween(row, 0.08, {Size = UDim2.new(1, -4, 0, 40), Position = UDim2.fromOffset(2, 1)}) end)
    click.MouseButton1Up:Connect(function() Tween(row, 0.14, {Size = UDim2.new(1, 0, 0, 42), Position = UDim2.new()}) end)
    click.MouseButton1Click:Connect(function()
        if action then action() else Toast("UI Preview", label .. " is visual-only in this mockup.") end
    end)
    row.MouseEnter:Connect(function()
        if emphasized then
            Tween(row, 0.16, {BackgroundColor3 = getAccentHover(), BackgroundTransparency = 0})
        else
            Tween(row, 0.16, {BackgroundColor3 = T.ElementHoverBG, BackgroundTransparency = 0})
        end
    end)
    row.MouseLeave:Connect(function()
        if emphasized then
            Tween(row, 0.16, {BackgroundColor3 = T.Accent, BackgroundTransparency = 0.08})
        else
            Tween(row, 0.16, {BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06})
        end
    end)
    BindTheme(function()
        if row.Parent then
            if emphasized then
                row.BackgroundColor3 = T.Accent
                text.TextColor3 = getAccentTextColor()
            else
                row.BackgroundColor3 = T.ElementBG
                text.TextColor3 = T.TextMain
            end
            outline.Color = T.Border
        end
    end)
end

local function Textbox(page, parentCard, label, placeholder, default, onChanged)
    local row = ElementBase(page, parentCard, label, 50)
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.42, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local box = New("TextBox", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(150, 28), BackgroundColor3 = T.ControlBG,
        Text = tostring(default or ""), PlaceholderText = placeholder or "Enter text...",
        PlaceholderColor3 = T.TextSub, TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
        TextSize = 10, ClearTextOnFocus = false, ZIndex = 10,
    })
    Corner(box, 9)
    Stroke(box, "Border", 0.35)
    box.FocusLost:Connect(function(enterPressed)
        if onChanged then
            task.spawn(function() pcall(onChanged, box.Text, enterPressed) end)
        end
    end)
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            box.BackgroundColor3 = T.ControlBG
            box.TextColor3 = T.TextMain
            box.PlaceholderColor3 = T.TextSub
        end
    end)
    return {
        Get = function() return box.Text end,
        Set = function(text, fire)
            box.Text = tostring(text or "")
            if fire and onChanged then
                task.spawn(function() pcall(onChanged, box.Text, false) end)
            end
        end,
    }
end

local function Keybind(page, parentCard, label, defaultKey, onChanged)
    local current = defaultKey or Enum.KeyCode.E
    if typeof(current) == "string" then
        current = Enum.KeyCode[current] or Enum.KeyCode.E
    end
    local listening = false
    local row = ElementBase(page, parentCard, label, 44)
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.48, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local box = New("TextButton", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(110, 28), BackgroundColor3 = T.ControlBG,
        Text = current and current.Name or "None", TextColor3 = T.Accent, Font = Enum.Font.GothamMedium,
        TextSize = 10, AutoButtonColor = false, ZIndex = 10,
    })
    Corner(box, 9)
    Stroke(box, "Border", 0.35)
    local function SetKey(key, fire)
        current = key
        box.Text = (key and key.Name) or "None"
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, current) end)
        end
    end
    box.MouseButton1Click:Connect(function()
        listening = true
        box.Text = "..."
    end)
    table.insert(connections, UserInputService.InputBegan:Connect(function(input, gpe)
        if not listening then return end
        if input.UserInputType == Enum.UserInputType.Keyboard or input.UserInputType == Enum.UserInputType.Gamepad1 then
            listening = false
            SetKey(input.KeyCode, true)
        end
    end))
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            box.BackgroundColor3 = T.ControlBG
            box.TextColor3 = T.Accent
        end
    end)
    return {
        Get = function() return current end,
        Set = function(key, fire) SetKey(key, fire == nil and true or fire) end,
    }
end

-- Colorpicker: swatch row + popup with R/G/B sliders, preset swatches and hex preview
local function Colorpicker(page, parentCard, label, defaultColor, onChanged)
    local color = defaultColor or Color3.fromRGB(255, 255, 255)
    local row = ElementBase(page, parentCard, label, 44)
    row.ClipsDescendants = false
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.48, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 2,
    })
    local swatch = New("TextButton", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(52, 24), BackgroundColor3 = color, Text = "",
        AutoButtonColor = false, ZIndex = 10,
    })
    Corner(swatch, 8)
    Stroke(swatch, "Border", 0.2)

    local PANEL_W, PANEL_H = 216, 158
    local panel = New("Frame", {
        Parent = DropdownLayer, Size = UDim2.fromOffset(PANEL_W, PANEL_H),
        BackgroundColor3 = T.SectionBG, BorderSizePixel = 0, Visible = false, ZIndex = 400,
    })
    Corner(panel, 12)
    Stroke(panel, "Border", 0.08)

    local preview = New("Frame", {
        Parent = panel, Position = UDim2.fromOffset(12, 10), Size = UDim2.new(1, -24, 0, 24),
        BackgroundColor3 = color, BorderSizePixel = 0, ZIndex = 401,
    })
    Corner(preview, 7)
    Stroke(preview, "Border", 0.35)
    local previewText = New("TextLabel", {
        Parent = preview, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Font = Enum.Font.GothamSemibold, TextSize = 11, ZIndex = 402,
        Text = "", TextColor3 = Color3.new(1, 1, 1),
    })

    local function ContrastTextColor(c)
        local lum = 0.299 * c.R + 0.587 * c.G + 0.114 * c.B
        return (lum > 0.55) and Color3.fromRGB(30, 32, 40) or Color3.fromRGB(255, 255, 255)
    end
    local function HexString(c)
        return string.format("#%02X%02X%02X",
            math.floor(c.R * 255 + 0.5), math.floor(c.G * 255 + 0.5), math.floor(c.B * 255 + 0.5))
    end

    local channelControl = {}
    local function ChannelRow(index, chName, chColor, yOffset)
        New("TextLabel", {
            Parent = panel, Position = UDim2.fromOffset(12, yOffset), Size = UDim2.fromOffset(18, 20),
            BackgroundTransparency = 1, Text = chName, Font = Enum.Font.GothamBold,
            TextSize = 11, TextColor3 = chColor, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 401,
        })
        local track = New("TextButton", {
            Parent = panel, Position = UDim2.new(0, 34, 0, yOffset + 6), Size = UDim2.new(1, -46, 0, 8),
            BackgroundColor3 = T.ControlBG, Text = "", AutoButtonColor = false, ZIndex = 401,
        })
        Corner(track, 4)
        local fill = New("Frame", {
            Parent = track, Size = UDim2.new(0, 0, 1, 0), BackgroundColor3 = chColor,
            BorderSizePixel = 0, ZIndex = 402,
        })
        Corner(fill, 4)
        local knob = New("Frame", {
            Parent = fill, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(1, 0.5),
            Size = UDim2.fromOffset(11, 11), BackgroundColor3 = Color3.new(1, 1, 1), ZIndex = 403,
        })
        Corner(knob, 6)
        local drag = false
        local function alphaFromInput(x)
            return math.clamp((x - track.AbsolutePosition.X) / math.max(track.AbsoluteSize.X, 1), 0, 1)
        end
        track.InputBegan:Connect(function(input)
            if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
                drag = true
                channelControl.SetChannelAlpha(index, alphaFromInput(input.Position.X), true)
            end
        end)
        table.insert(connections, UserInputService.InputChanged:Connect(function(input)
            if drag and (input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch) then
                channelControl.SetChannelAlpha(index, alphaFromInput(input.Position.X), true)
            end
        end))
        table.insert(connections, UserInputService.InputEnded:Connect(function(input)
            if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
                drag = false
            end
        end))
        return fill
    end

    local rFill = ChannelRow(1, "R", Color3.fromRGB(244, 92, 92), 44)
    local gFill = ChannelRow(2, "G", Color3.fromRGB(92, 214, 131), 70)
    local bFill = ChannelRow(3, "B", Color3.fromRGB(92, 147, 244), 96)

    local presets = {
        Color3.fromRGB(150, 150, 150), Color3.fromRGB(255, 84, 84), Color3.fromRGB(255, 176, 66),
        Color3.fromRGB(255, 235, 110), Color3.fromRGB(96, 214, 128), Color3.fromRGB(66, 153, 255),
        Color3.fromRGB(154, 110, 255), Color3.fromRGB(255, 110, 200), Color3.fromRGB(255, 255, 255),
    }
    for i, presetColor in ipairs(presets) do
        local presetBtn = New("TextButton", {
            Parent = panel, Position = UDim2.fromOffset(12 + (i - 1) * 20, 126), Size = UDim2.fromOffset(16, 16),
            BackgroundColor3 = presetColor, Text = "", AutoButtonColor = false, ZIndex = 401,
        })
        Corner(presetBtn, 5)
        Stroke(presetBtn, "Border", 0.35)
        presetBtn.MouseButton1Click:Connect(function()
            channelControl.SetColor(presetColor, true)
        end)
    end

    function channelControl.SetColor(newColor, fire)
        color = newColor
        swatch.BackgroundColor3 = color
        preview.BackgroundColor3 = color
        previewText.Text = HexString(color)
        previewText.TextColor3 = ContrastTextColor(color)
        rFill.Size = UDim2.new(color.R, 0, 1, 0)
        gFill.Size = UDim2.new(color.G, 0, 1, 0)
        bFill.Size = UDim2.new(color.B, 0, 1, 0)
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, color) end)
        end
    end
    function channelControl.SetChannelAlpha(index, alpha, fire)
        local r, g, b = color.R, color.G, color.B
        if index == 1 then r = alpha
        elseif index == 2 then g = alpha
        else b = alpha end
        channelControl.SetColor(Color3.new(r, g, b), fire)
    end

    local function PositionPanel()
        local windowPos = Window.AbsolutePosition
        local layerSize = DropdownLayer.AbsoluteSize
        local x = (swatch.AbsolutePosition.X - windowPos.X) + swatch.AbsoluteSize.X - PANEL_W
        local y = (swatch.AbsolutePosition.Y - windowPos.Y) + swatch.AbsoluteSize.Y + 6
        if y + PANEL_H > layerSize.Y - 8 then
            y = (swatch.AbsolutePosition.Y - windowPos.Y) - PANEL_H - 6
        end
        x = math.clamp(x, 8, math.max(8, layerSize.X - PANEL_W - 8))
        y = math.clamp(y, 8, math.max(8, layerSize.Y - PANEL_H - 8))
        panel.Position = UDim2.fromOffset(x, y)
    end

    swatch.MouseButton1Click:Connect(function()
        if activeDropdown then activeDropdown.Visible = false; activeDropdown = nil end
        if activeColorPopup and activeColorPopup ~= panel then activeColorPopup.Visible = false end
        PositionPanel()
        panel.Visible = not panel.Visible
        activeColorPopup = panel.Visible and panel or nil
        DropdownLayer.Visible = panel.Visible
    end)

    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            panel.BackgroundColor3 = T.SectionBG
        end
    end)

    channelControl.SetColor(color, false)

    return {
        Get = function() return color end,
        Set = function(value, fire)
            if typeof(value) == "Color3" then
                channelControl.SetColor(value, fire == nil and true or fire)
            end
        end,
    }
end

local function Divider(page, parentCard)
    local row = New("Frame", {
        Parent = parentCard, Size = UDim2.new(1, 0, 0, 12), BackgroundTransparency = 1,
    })
    local line = New("Frame", {
        Parent = row, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(0.5, 0.5),
        Size = UDim2.new(1, -8, 0, 1), BackgroundColor3 = T.Border, BorderSizePixel = 0,
    })
    BindTheme(function()
        if line.Parent then line.BackgroundColor3 = T.Border end
    end)
    return row
end

local function StatGrid(page, stats)
    local grid = New("Frame", {
        Parent = page.Body, Size = UDim2.new(1, 0, 0, 92), BackgroundTransparency = 1,
    })
    local layout = New("UIGridLayout", {
        Parent = grid, CellPadding = UDim2.fromOffset(10, 0),
        CellSize = UDim2.new(0.25, -8, 1, 0), SortOrder = Enum.SortOrder.LayoutOrder,
    })
    for index, stat in ipairs(stats) do
        local card = New("Frame", {
            Parent = grid, LayoutOrder = index, BackgroundColor3 = T.SectionBG,
            BackgroundTransparency = 0.07,
        })
        Corner(card, 14); Gradient(card, {function(t) return Mix(t.SectionBG, t.Accent3, .12) end, function(t) return Mix(t.SectionBG, t.Accent2, .10) end}, 20); Stroke(card, "Border", 0.18)
        local value = New("TextLabel", {
            Parent = card, Position = UDim2.fromOffset(14, 14), Size = UDim2.new(1, -28, 0, 28),
            BackgroundTransparency = 1, Text = stat[2], Font = Enum.Font.GothamBold,
            TextSize = 21, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
        })
        local label = New("TextLabel", {
            Parent = card, Position = UDim2.fromOffset(14, 48), Size = UDim2.new(1, -28, 0, 18),
            BackgroundTransparency = 1, Text = stat[1], Font = Enum.Font.Gotham,
            TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        })
        BindTheme(function()
            if card.Parent then card.BackgroundColor3 = T.SectionBG; value.TextColor3 = T.Accent; label.TextColor3 = T.TextSub end
        end)
    end
    return layout
end

local function TwoColumnRow(page)
    local row = New("Frame", {
        Parent = page.Body, Size = UDim2.new(1, 0, 0, 0),
        BackgroundTransparency = 1,
    })
    New("UIListLayout", {
        Parent = row, FillDirection = Enum.FillDirection.Horizontal, Padding = UDim.new(0, 14),
        SortOrder = Enum.SortOrder.LayoutOrder,
    })
    local left = New("Frame", {
        Parent = row, Size = UDim2.new(0.5, -20, 0, 0), AutomaticSize = Enum.AutomaticSize.Y,
        BackgroundTransparency = 1,
    })
    local leftLayout = New("UIListLayout", {Parent = left, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})
    local right = New("Frame", {
        Parent = row, Size = UDim2.new(0.5, -20, 0, 0), AutomaticSize = Enum.AutomaticSize.Y,
        BackgroundTransparency = 1,
    })
    local rightLayout = New("UIListLayout", {Parent = right, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})

    local function UpdateRowHeight()
        local height = math.max(leftLayout.AbsoluteContentSize.Y, rightLayout.AbsoluteContentSize.Y)
        row.Size = UDim2.new(1, 0, 0, height)
    end
    UpdateRowHeight()
    table.insert(connections, leftLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(UpdateRowHeight))
    table.insert(connections, rightLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(UpdateRowHeight))

    return left, right
end

local function HomeInfoRow(parent, labelText, valueText, accentValue)
    local row = New("Frame", {
        Parent = parent, Size = UDim2.new(1, 0, 0, 38),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.03,
    })
    Corner(row, 10)
    Gradient(row, {
        function(t) return Mix(t.ElementBG, t.Accent3, .06) end,
        "ElementBG",
        function(t) return Mix(t.ElementBG, t.Accent2, .05) end,
    }, 0)
    Stroke(row, "Border", 0.28)
    local label = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(12, 0), Size = UDim2.new(0.42, 0, 1, 0),
        BackgroundTransparency = 1, Text = labelText, Font = Enum.Font.Gotham,
        TextSize = 10, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local value = New("TextLabel", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -12, 0.5, 0),
        Size = UDim2.new(0.55, 0, 1, 0), BackgroundTransparency = 1, Text = valueText,
        Font = Enum.Font.GothamSemibold, TextSize = 10,
        TextColor3 = accentValue and T.Accent or T.TextMain,
        TextXAlignment = Enum.TextXAlignment.Right, TextTruncate = Enum.TextTruncate.AtEnd,
    })
    BindTheme(function()
        if row.Parent then
            row.BackgroundColor3 = T.ElementBG
            label.TextColor3 = T.TextSub
            value.TextColor3 = accentValue and T.Accent or T.TextMain
        end
    end)
    return {Frame = row, Label = label, Value = value}
end

local function HomeStatCard(parent, order, titleText, valueText, accentValue)
    local card = New("Frame", {
        Parent = parent, LayoutOrder = order, BackgroundColor3 = T.SectionBG,
        BackgroundTransparency = 0.05,
    })
    Corner(card, 14)
    Gradient(card, {
        function(t) return Mix(t.SectionBG, t.Accent3, .12) end,
        function(t) return Mix(t.SectionBG, t.Accent2, .10) end,
    }, 20)
    Stroke(card, "Border", 0.18)
    local value = New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(14, 16), Size = UDim2.new(1, -28, 0, 28),
        BackgroundTransparency = 1, Text = valueText, Font = Enum.Font.GothamBold,
        TextSize = 16, TextColor3 = accentValue and T.Accent or T.TextMain,
        TextXAlignment = Enum.TextXAlignment.Left, TextTruncate = Enum.TextTruncate.AtEnd,
    })
    local title = New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(14, 52), Size = UDim2.new(1, -28, 0, 18),
        BackgroundTransparency = 1, Text = titleText, Font = Enum.Font.Gotham,
        TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
    })
    BindTheme(function()
        if card.Parent then
            card.BackgroundColor3 = T.SectionBG
            value.TextColor3 = accentValue and T.Accent or T.TextMain
            title.TextColor3 = T.TextSub
        end
    end)
    return {Frame = card, Title = title, Value = value}
end

-- Pages --------------------------------------------------------------------
local function BuildHomePage()
local Home = CreatePage("Home")
local homeStats = New("Frame", {
    Parent = Home.Body, Size = UDim2.new(1, 0, 0, 92), BackgroundTransparency = 1,
})
New("UIGridLayout", {
    Parent = homeStats, CellPadding = UDim2.fromOffset(10, 0),
    CellSize = UDim2.new(0.25, -8, 1, 0), SortOrder = Enum.SortOrder.LayoutOrder,
})
local homeNameStat = HomeStatCard(homeStats, 1, "SCRIPT", "BLADE BALL LYRA", true)
local homeExecutorStat = HomeStatCard(homeStats, 2, "EXECUTOR", executorName, false)
homeThemeStat = HomeStatCard(homeStats, 3, "THEME", themeName, false)
local homeStatusStat = HomeStatCard(homeStats, 4, "STATUS", "LOADED", true)

local session = Section(Home, "Session details", "Current interface information.")
local sessionName = HomeInfoRow(session, "Script", "Blade Ball Lyra", true)
local sessionExecutor = HomeInfoRow(session, "Executor", executorName, false)
sessionTheme = HomeInfoRow(session, "Theme", themeName, false)
local sessionStatus = HomeInfoRow(session, "Status", "Loaded", true)
local sessionPlayer = HomeInfoRow(session, "Player", LocalPlayer.DisplayName, false)

local runtime = Section(Home, "Status", "Live UI state and layout info.")
local runtimeScale = HomeInfoRow(runtime, "Responsive Scale", "Enabled", true)
local runtimeSidebar = HomeInfoRow(runtime, "Sidebar", "Hover Expand", false)
local runtimeSearch = HomeInfoRow(runtime, "Search", "Ready", false)
runtimeTheme = HomeInfoRow(runtime, "Default Palette", themeName, false)
local runtimeMode = HomeInfoRow(runtime, "Mode", "Feature Build", false)
local runtimeWindow = HomeInfoRow(runtime, "Ping", "0 ms", false)

table.insert(connections, RunService.RenderStepped:Connect(function()
    if homeStatusStat and homeStatusStat.Value and homeStatusStat.Value.Parent then
        homeStatusStat.Value.Text = System.__properties.__autoparry_enabled and "ARMED" or "READY"
    end
    if sessionStatus and sessionStatus.Value and sessionStatus.Value.Parent then
        sessionStatus.Value.Text = System.__triggerbot.__enabled and "Triggerbot" or "Loaded"
    end
    if runtimeMode and runtimeMode.Value and runtimeMode.Value.Parent then
        runtimeMode.Value.Text = System.__properties.__manual_spam_enabled and "Spam Active" or "Feature Build"
    end
    if runtimeWindow and runtimeWindow.Value and runtimeWindow.Value.Parent then
        runtimeWindow.Value.Text = tostring(safePing()) .. " ms"
    end
end))

BindTheme(function()
    if homeThemeStat and homeThemeStat.Value.Parent then homeThemeStat.Value.Text = themeName end
    if sessionTheme and sessionTheme.Value.Parent then sessionTheme.Value.Text = themeName end
    if runtimeTheme and runtimeTheme.Value.Parent then runtimeTheme.Value.Text = themeName end
end)
end
BuildHomePage()

local pageDefinitions = {
    Combat = {
        {"Autoparry", "Core parry logic, trigger modes, and ability support.", {
            {"toggle", "Enable Autoparry", false, function(state)
                System.__properties.__autoparry_enabled = state
                if state then
                    System.autoparry.start()
                    notify("Autoparry", "Enabled", 3)
                else
                    System.autoparry.stop()
                    notify("Autoparry", "Disabled", 3)
                end
            end},
            {"dropdown", "Parry Method", {"Combined (Mobile)", "Screen Tap", "Block Button", "Direct Call (PF)", "KeyPress (F)", "MouseClick"}, getgenv().AutoParryMode or "Combined (Mobile)", function(value)
                getgenv().AutoParryMode = value
                notify("Parry Method", value, 2.5)
            end},
            {"toggle", "Triggerbot", false, function(state)
                System.triggerbot.enable(state)
                notify("Triggerbot", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Accuracy", 1, 10, 1, "", function(value)
                System.__properties.__accuracy = value
                update_divisor()
            end},
            {"toggle", "Play Parry Animation", false, function(state)
                System.__properties.__play_animation = state
            end},
            {"toggle", "Cooldown Protection", false, function(state)
                getgenv().CooldownProtection = state
            end},
            {"toggle", "Auto Ability", false, function(state)
                getgenv().AutoAbility = state
            end},
        }},
        {"Detections", "Handle Infinity, Death Slash, Time Hole, Slashes of Fury, and Phantom checks.", {
            {"toggle", "Infinity Detection", false, function(state) System.__config.__detections.__infinity = state end},
            {"toggle", "Deathslash Detection", false, function(state) System.__config.__detections.__deathslash = state end},
            {"toggle", "Timehole Detection", false, function(state) System.__config.__detections.__timehole = state end},
            {"toggle", "Slashes Of Fury Detection", false, function(state) System.__config.__detections.__slashesoffury = state end},
            {"toggle", "Phantom Detection", false, function(state) System.__config.__detections.__phantom = state end},
        }},
        {"Spam", "Manual, hold, and automatic spam controls.", {
            {"toggle", "Always Spam", false, function(state)
                System.__properties.__manual_spam_enabled = state
                notify("Manual Spam", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Hold To Spam", false, function(state)
                getgenv().HoldSpamEnabled = state
                notify("Hold Spam", state and "Enabled" or "Disabled", 3)
            end},
            {"textbox", "Hold Spam Key", "V", getgenv().HoldSpamKey or "V", function(value)
                local key = string.upper((value or "V"):gsub("%s+", ""))
                if key == "" then key = "V" end
                getgenv().HoldSpamKey = key
            end},
            {"toggle", "Auto Spam", false, function(state)
                if state then
                    System.auto_spam.start()
                    notify("Auto Spam", "Enabled", 3)
                else
                    System.auto_spam.stop()
                    notify("Auto Spam", "Disabled", 3)
                end
            end},
            {"slider", "Spam Rate", 10, 300, 240, "", function(value)
                System.__properties.__spam_rate = value
            end},
        }},
    },
    Visuals = {
        {"ESP", "Highlight enemies and control simple visibility features.", {
            {"toggle", "Enable ESP", false, function(state)
                System.visuals.__esp_enabled = state
                notify("ESP", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Team Check", false, function(state)
                System.visuals.__esp_team_check = state
            end},
        }},
        {"Environment", "Lighting and ball effect visuals.", {
            {"toggle", "Night Mode", false, function(state)
                System.visuals.toggle_night_mode(state)
                notify("Night Mode", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Rain Effect", false, function(state)
                Features.Visuals.Rain.Enabled = state
                notify("Rain", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Max Rain Particles", 1000, 10000, 5000, "", function(value)
                Features.Visuals.Rain.MaxParticles = value
            end},
            {"slider", "Rain Fall Speed", 10, 50, 25, "", function(value)
                Features.Visuals.Rain.FallSpeed = value
            end},
            {"toggle", "Plasma Trails", false, function(state)
                Features.Visuals.Plasma.Enabled = state
                notify("Plasma", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Trail Count", 4, 16, 8, "", function(value)
                Features.Visuals.Plasma.NumTrails = math.floor(value)
            end},
        }},
    },
    Player = {
        {"Movement", "WalkSpeed and JumpPower modifiers.", {
            {"toggle", "Custom WalkSpeed", false, function(state) System.player_mods.__walkspeed_enabled = state end},
            {"slider", "WalkSpeed", 16, 100, 16, "", function(value) System.player_mods.__walkspeed_value = value end},
            {"toggle", "Custom JumpPower", false, function(state) System.player_mods.__jumppower_enabled = state end},
            {"slider", "JumpPower", 50, 200, 50, "", function(value) System.player_mods.__jumppower_value = value end},
        }},
        {"Camera", "FOV, spin, and camera shake options.", {
            {"toggle", "Disable Cam Shake", false, function(state) System.player_mods.disable_camera_shake(state) end},
            {"toggle", "FOV Mod", false, function(state) System.player_mods.__fov_enabled = state end},
            {"slider", "FOV", 70, 120, 70, "", function(value) System.player_mods.__fov_value = value end},
            {"toggle", "Spinbot", false, function(state)
                System.player_mods.__spinbot_enabled = state
                notify("Spinbot", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Spin Speed", 10, 100, 50, "", function(value) System.player_mods.__spinbot_speed = value end},
        }},
    },
    Utility = {
        {"Emotes", "Play and manage available emotes.", {
            {"toggle", "Enable Emote System", false, function(state)
                if state then
                    animation_system.start()
                    notify("Emote System", "Enabled", 3)
                else
                    animation_system.cleanup()
                    notify("Emote System", "Disabled", 3)
                end
            end},
            {"textbox", "Search Emote", "Type and press enter", "", function(value, enterPressed)
                if not enterPressed or value == "" then return end
                local lower = string.lower(value)
                for _, emote in ipairs(animation_system.get_emotes_list()) do
                    if string.lower(emote):find(lower, 1, true) then
                        animation_system.play(emote)
                        animation_system.current = emote
                        notify("Emote", "Playing: " .. emote, 3)
                        return
                    end
                end
                notify("Emote", "No emote matched that search.", 3)
            end},
            {"dropdown", "Select Emote", emotes_data, emotes_data[1] or "None", function(value)
                if value ~= "None" then
                    animation_system.play(value)
                    animation_system.current = value
                    notify("Emote", "Playing: " .. value, 3)
                end
            end},
            {"toggle", "Auto Stop On Move", getgenv().AutoStopEmote == true, function(state)
                getgenv().AutoStopEmote = state
            end},
            {"button", "Stop Current Emote", false, function()
                animation_system.stop()
                notify("Emote", "Stopped", 2.5)
            end},
        }},
        {"Skin Changer", "Local sword model, animation, and FX customization.", {
            {"toggle", "Enable Skin Changer", false, function(state)
                getgenv().skinChangerEnabled = state
                if state and getgenv().updateSword then pcall(getgenv().updateSword) end
                notify("Skin Changer", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Change Sword Model", false, function(state) getgenv().changeSwordModel = state end},
            {"textbox", "Sword Model Name", "Ex: Excalibur", getgenv().swordModel or "", function(value) getgenv().swordModel = value end},
            {"toggle", "Change Sword Animation", false, function(state) getgenv().changeSwordAnimation = state end},
            {"textbox", "Sword Anim Name", "Ex: DualWield", getgenv().swordAnimations or "", function(value) getgenv().swordAnimations = value end},
            {"toggle", "Change Sword FX", false, function(state) getgenv().changeSwordFX = state end},
            {"textbox", "Sword FX Name", "Ex: Fire", getgenv().swordFX or "", function(value) getgenv().swordFX = value end},
            {"button", "Refresh Sword Visuals", false, function()
                if getgenv().updateSword then
                    pcall(getgenv().updateSword)
                    notify("Skin Changer", "Refreshed sword visuals.", 3)
                else
                    notify("Skin Changer", "No sword refresh function found.", 3)
                end
            end},
        }},
    },
    Exclusive = {
        {"Walkable Immortal", "Semi-immortal desync controls.", {
            {"toggle", "Enable Walkable Immortal", false, function(state)
                WalkableSemiImmortal.toggle(state)
                notify("Walkable Immortal", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Radius", 0, 100, 25, "", function(value) WalkableSemiImmortal.setRadius(value) end},
            {"slider", "Height", 0, 60, 30, "", function(value) WalkableSemiImmortal.setHeight(value) end},
        }},
        {"Target Player", "Pick a player by username or display name.", {
            {"toggle", "Enable Target Player", false, function(state)
                Features.TargetPlayer.Enabled = state
                notify("Target Player", state and "Enabled" or "Disabled", 3)
            end},
            {"textbox", "Player Name", "Username or display name", "", function(value)
                Features.TargetPlayer.setTarget(value)
            end},
        }},
        {"Avatar Changer", "Apply another player's avatar onto your character locally.", {
            {"textbox", "Target Username", "Username...", getgenv().PendingAvatarTarget or "", function(value)
                getgenv().PendingAvatarTarget = value
            end},
            {"button", "Apply Avatar", true, function()
                local target = getgenv().PendingAvatarTarget or ""
                if target == "" then
                    notify("Avatar Changer", "Enter a username first.", 3)
                    return
                end
                notify("Avatar Changer", "Attempting to change avatar...", 3)
                Features.AvatarChanger.setAvatar(target)
            end},
        }},
    },
}

local function AddControl(page, card, definition)
    local control
    if definition[1] == "toggle" then
        control = Toggle(page, card, definition[2], definition[3], definition[4])
    elseif definition[1] == "slider" then
        control = Slider(page, card, definition[2], definition[3], definition[4], definition[5], definition[6], definition[7])
    elseif definition[1] == "dropdown" then
        control = Dropdown(page, card, definition[2], definition[3], definition[4], definition[5])
    elseif definition[1] == "button" then
        control = Button(page, card, definition[2], definition[3], definition[4])
    elseif definition[1] == "textbox" then
        control = Textbox(page, card, definition[2], definition[3], definition[4], definition[5])
    end
    if control and type(control) == "table" and definition[2] then
        ConfigManager.Register(definition[2], control)
    end
    return control
end

for pageName, sections in pairs(pageDefinitions) do
    local page = CreatePage(pageName)
    for _, definition in ipairs(sections) do
        local card = Section(page, definition[1], definition[2])
        for _, control in ipairs(definition[3]) do
            AddControl(page, card, control)
        end
    end
end

local function BuildSettingsPage()
local Settings = CreatePage("Settings")
local appearance = Section(Settings, "Appearance", "Theme and accent presentation.")
ConfigManager.Register("UI Theme", Dropdown(Settings, appearance, "UI Theme", {"Midnight Violet", "Rose Pine", "Catppuccin", "Dracula", "Tokyo Night", "Minimal White", "Aurora Gray", "Hello Kitty"}, themeName, function(value)
    themeName = value; T = Themes[value]
    if homeThemeStat and homeThemeStat.Value.Parent then homeThemeStat.Value.Text = value end
    if sessionTheme and sessionTheme.Value.Parent then sessionTheme.Value.Text = value end
    if runtimeTheme and runtimeTheme.Value.Parent then runtimeTheme.Value.Text = value end
    for _, repaint in ipairs(themeBindings) do pcall(repaint) end
    Toast("Theme changed", value .. " palette applied.")
end))
ConfigManager.Register("Rainbow Accent", Toggle(Settings, appearance, "Rainbow Accent", false))
ConfigManager.Register("Rainbow Speed", Slider(Settings, appearance, "Rainbow Speed", 1, 20, 5, ""))
ConfigManager.Register("Window Scale", Slider(Settings, appearance, "Window Scale", 50, 150, 100, "%", function(value)
    uiScaleFactor = math.clamp(value / 100, 0.55, 1.25)
    task.defer(function()
        if Scale and GetTargetScale then
            Scale.Scale = GetTargetScale()
        end
    end)
end))
local behavior = Section(Settings, "Window & navigation", "General interface preferences.")
ConfigManager.Register("Notifications & Tooltips", Toggle(Settings, behavior, "Notifications & Tooltips", true, function(value)
    notificationsEnabled = value
end))
ConfigManager.Register("Expand Sidebar on Hover", Toggle(Settings, behavior, "Expand Sidebar on Hover", true, function(value)
    sidebarHoverEnabled = value
    if not value then
        SetSidebar(true)
    end
end))
ConfigManager.Register("Menu Keybind", Dropdown(Settings, behavior, "Menu Keybind", {"RightControl", "Insert", "Home", "F4"}, "RightControl", function(value)
    menuToggleKey = Enum.KeyCode[value] or Enum.KeyCode.RightControl
    notify("Menu Keybind", value, 2.5)
end))
Button(Settings, behavior, "Preview notification", true, function() notify("Blade Ball Lyra", "Blade Ball is ready.", 3) end)

-- Per-script configs (Lyra/Blade Ball/Configs + Autoload.txt)
local configs = Section(Settings, "Configs", "Configs for " .. SCRIPT_ID .. " only — never shared with other Lyra scripts.")
local configName = "default"
local configNameBox = ConfigManager.Register("Config Name", Textbox(Settings, configs, "Config Name", "default", "default", function(text)
    configName = tostring(text or "default")
end))
local autoloadToggle
autoloadToggle = ConfigManager.Register("Autoload", Toggle(Settings, configs, "Autoload", ConfigManager.GetAutoload() ~= nil, function(state)
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    ConfigManager.SetAutoload(configName, state)
    notify("Autoload", state and ("Will load '" .. configName .. "' for " .. SCRIPT_ID) or "Autoload disabled", 3)
end))
local viewConfigs
local function refreshConfigDropdown()
    local list = ConfigManager.List()
    if #list == 0 then list = {"default"} end
    if viewConfigs and viewConfigs.Refresh then viewConfigs.Refresh(list) end
    return list
end
viewConfigs = ConfigManager.Register("View Configs", Dropdown(Settings, configs, "View Configs", refreshConfigDropdown(), "default", function(value)
    configName = value
    if configNameBox and configNameBox.Set then configNameBox.Set(value, false) end
    if autoloadToggle and autoloadToggle.Set then
        autoloadToggle.Set(ConfigManager.GetAutoload() == value, false)
    end
end))
Button(Settings, configs, "Save Config", true, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, err = ConfigManager.Save(configName)
    refreshConfigDropdown()
    notify("Configs", ok and ("Saved '" .. configName .. "' → " .. ConfigManager.Folder) or tostring(err), 4)
end)
Button(Settings, configs, "Load Config", false, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, res = ConfigManager.Load(configName)
    notify("Configs", ok and ("Loaded '" .. configName .. "' (" .. tostring(res) .. " flags)") or tostring(res), 4)
end)
Button(Settings, configs, "Delete Config", false, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, err = ConfigManager.Delete(configName)
    refreshConfigDropdown()
    notify("Configs", ok and ("Deleted '" .. configName .. "'") or tostring(err), 3)
end)
end
BuildSettingsPage()



-- ensure notify works with Toast
notify = function(title, text, duration)
    if type(Toast) == "function" then
        Toast(tostring(title or "Notice"), tostring(text or ""), duration or 3)
    end
end

local function FinalizeUI()
-- Theme bindings for primary shell -----------------------------------------
BindTheme(function()
    if destroyed then return end
    Dim.BackgroundColor3 = T.MainBG
    Dim.BackgroundTransparency = 1
    Window.BackgroundColor3 = T.MainBG
    Sidebar.BackgroundColor3 = T.SidebarBG
    sideLine.BackgroundColor3 = T.Border
    Topbar.BackgroundColor3 = T.TopbarBG
    topLine.BackgroundColor3 = T.Border
    GlowOne.BackgroundColor3 = T.Accent
    BrandLogoShell.BackgroundColor3 = T.ElementBG
    BrandTitle.TextColor3 = T.TextMain; BrandSub.TextColor3 = T.Accent
    Profile.BackgroundColor3 = T.ElementBG; Avatar.BackgroundColor3 = T.ControlBG
    ProfileName.TextColor3 = T.TextMain; ProfileStatus.TextColor3 = T.Accent
    PageTitle.TextColor3 = T.TextMain; Breadcrumb.TextColor3 = T.TextSub
    SearchBox.BackgroundColor3 = T.ControlBG; SearchIcon.ImageColor3 = T.TextSub
    SearchInput.TextColor3 = T.TextMain; SearchInput.PlaceholderColor3 = T.TextSub
    MinimizeButton.BackgroundColor3 = T.ControlBG; MinimizeButton.ImageColor3 = T.TextSub
    CloseButton.BackgroundColor3 = T.ControlBG; CloseButton.ImageColor3 = T.TextSub
    OpenButton.BackgroundColor3 = T.MainBG
    windowStroke.Color = T.Border
end)

-- Navigation ---------------------------------------------------------------
local navItems = {
    {"Home", ICONS.Home},
    {"Combat", ICONS.Defense},
    {"Visuals", ICONS.Visual},
    {"Player", ICONS.Player},
    {"Utility", ICONS.Misc},
    {"Exclusive", ICONS.Trolling},
    {"Settings", ICONS.Settings},
}

SetSidebar = function(expanded)
    if sidebarExpanded == expanded then return end
    sidebarExpanded = expanded
    Tween(Sidebar, 0.28, {Size = UDim2.new(0, expanded and SIDEBAR_OPEN or SIDEBAR_COMPACT, 1, 0)})
    ApplyContentLayout(expanded, false)
    Tween(BrandTitle, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(BrandSub, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(ProfileName, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(ProfileStatus, 0.18, {TextTransparency = expanded and 0 or 1})
    for _, item in pairs(navEntries) do
        Tween(item.Label, 0.18, {TextTransparency = expanded and 0 or 1})
    end
end

local function SelectPage(name)
    local targetPage = pages[name]
    if not targetPage then return end
    if currentPage == name and targetPage.Group.Visible then return end

    if activeDropdown then activeDropdown.Visible = false; activeDropdown = nil end
    if activeColorPopup then activeColorPopup.Visible = false; activeColorPopup = nil end
    DropdownLayer.Visible = false

    local previousName = currentPage
    local previousPage = previousName and pages[previousName] or nil
    currentPage = name

    if previousPage and previousPage.Group.Visible then
        Tween(previousPage.Group, 0.18, {
            GroupTransparency = 1,
            Position = UDim2.fromOffset(-14, 0)
        })

        task.delay(0.19, function()
            if previousPage.Group and previousPage.Group.Parent then
                previousPage.Group.Visible = false
                previousPage.Group.GroupTransparency = 1
            end
        end)
    end

    targetPage.Group.Visible = true
    targetPage.Group.Position = UDim2.fromOffset(18, 0)
    targetPage.Group.GroupTransparency = 1

    if targetPage.Scroll then
        targetPage.Scroll.CanvasPosition = Vector2.new(0, 0)
    end

    -- With AutomaticCanvasSize = Y, the scroll frame handles canvas sizing automatically.
    -- No manual Refresh needed.

    Tween(targetPage.Group, 0.3, {
        GroupTransparency = 0,
        Position = UDim2.fromOffset(0, 0)
    })

    PageTitle.Text = name == "Home" and "Status" or name
    Breadcrumb.Text = name == "Home" and "Lyra  /  Status" or ("Lyra  /  " .. name)
    SearchInput.Text = ""

    for navName, item in pairs(navEntries) do
        local selected = navName == name
        Tween(item.Button, 0.18, {
            BackgroundTransparency = selected and 0.04 or 1,
            BackgroundColor3 = selected and T.ElementBG or T.SidebarBG,
        })
        Tween(item.Icon, 0.18, {ImageColor3 = selected and T.Accent or T.TextSub})
        Tween(item.Label, 0.18, {TextColor3 = selected and T.TextMain or T.TextSub})
        Tween(item.Indicator, 0.18, {BackgroundTransparency = selected and 0 or 1, Size = UDim2.fromOffset(3, selected and 22 or 4)})
    end
end

for index, definition in ipairs(navItems) do
    local name, icon = definition[1], definition[2]
    local button = New("TextButton", {
        Parent = NavScroll, LayoutOrder = index, Size = UDim2.new(1, 0, 0, 43),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 23,
    })
    Corner(button, 11)
    Gradient(button, {function(t) return Mix(t.ElementBG, t.Accent3, .16) end, function(t) return Mix(t.ElementBG, t.Accent2, .13) end}, 0)
    local indicator = New("Frame", {
        Parent = button, AnchorPoint = Vector2.new(0, 0.5), Position = UDim2.new(0, 0, 0.5, 0),
        Size = UDim2.fromOffset(3, 4), BackgroundColor3 = T.Accent,
        BackgroundTransparency = 1, BorderSizePixel = 0, ZIndex = 24,
    })
    Corner(indicator, 3)
    Gradient(indicator, {"Accent3", "Accent", "Accent2"}, 90)
    local image = New("ImageLabel", {
        Parent = button, Position = UDim2.fromOffset(14, 11), Size = UDim2.fromOffset(21, 21),
        BackgroundTransparency = 1, Image = icon, ImageColor3 = T.TextSub, ZIndex = 24,
    })
    local label = New("TextLabel", {
        Parent = button, Position = UDim2.fromOffset(48, 0), Size = UDim2.new(1, -52, 1, 0),
        BackgroundTransparency = 1, Text = name, Font = Enum.Font.GothamMedium,
        TextSize = 11, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        TextTransparency = 1, ZIndex = 24,
    })
    navEntries[name] = {Button = button, Icon = image, Label = label, Indicator = indicator}
    button.MouseEnter:Connect(function()
        if currentPage ~= name then Tween(button, 0.15, {BackgroundTransparency = 0.55}) end
    end)
    button.MouseLeave:Connect(function()
        if currentPage ~= name then Tween(button, 0.15, {BackgroundTransparency = 1}) end
    end)
    button.MouseButton1Click:Connect(function() SelectPage(name) end)
    BindTheme(function()
        if button.Parent then
            local selected = currentPage == name
            button.BackgroundColor3 = selected and T.ElementBG or T.SidebarBG
            image.ImageColor3 = selected and T.Accent or T.TextSub
            label.TextColor3 = selected and T.TextMain or T.TextSub
            indicator.BackgroundColor3 = T.Accent
        end
    end)
end

Sidebar.MouseEnter:Connect(function() if sidebarHoverEnabled then SetSidebar(true) end end)
Sidebar.MouseLeave:Connect(function() if sidebarHoverEnabled then SetSidebar(false) end end)

SearchInput:GetPropertyChangedSignal("Text"):Connect(function()
    local query = string.lower(SearchInput.Text)
    local page = pages[currentPage]
    if not page then return end
    for _, searchable in ipairs(page.Searchables) do
        searchable.Object.Visible = query == "" or string.find(searchable.Text, query, 1, true) ~= nil
    end
end)

-- Dragging -----------------------------------------------------------------
local dragging, dragStart, startPosition, dragInput
Topbar.InputBegan:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
        dragging = true; dragStart = input.Position; startPosition = Window.Position
        input.Changed:Connect(function()
            if input.UserInputState == Enum.UserInputState.End then dragging = false end
        end)
    end
end)
Topbar.InputChanged:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch then dragInput = input end
end)
table.insert(connections, UserInputService.InputChanged:Connect(function(input)
    if dragging and input == dragInput then
        local delta = input.Position - dragStart
        Window.Position = UDim2.new(startPosition.X.Scale, startPosition.X.Offset + delta.X, startPosition.Y.Scale, startPosition.Y.Offset + delta.Y)
    end
end))

-- Window behavior ----------------------------------------------------------
local windowOpen = true
local function SetOpen(open)
    windowOpen = open
    if open then
        local targetScale = GetTargetScale()
        Window.Visible = true; Dim.Visible = true
        Window.GroupTransparency = 1; Scale.Scale = targetScale * 0.94
        Tween(Window, 0.28, {GroupTransparency = 0})
        Tween(Scale, 0.3, {Scale = targetScale})
        OpenButton.Visible = false
    else
        if activeDropdown then activeDropdown.Visible = false; activeDropdown = nil end
        if activeColorPopup then activeColorPopup.Visible = false; activeColorPopup = nil end
        DropdownLayer.Visible = false
        Tween(Window, 0.22, {GroupTransparency = 1})
        task.delay(0.22, function()
            if not windowOpen and Window.Parent then Window.Visible = false; Dim.Visible = false; OpenButton.Visible = true end
        end)
    end
end
MinimizeButton.MouseButton1Click:Connect(function() SetOpen(false) end)
OpenButton.MouseButton1Click:Connect(function() SetOpen(true) end)
OpenButton.MouseEnter:Connect(function() Tween(OpenButton, 0.16, {Size = UDim2.fromOffset(59, 59)}) end)
OpenButton.MouseLeave:Connect(function() Tween(OpenButton, 0.16, {Size = UDim2.fromOffset(54, 54)}) end)
CloseButton.MouseButton1Click:Connect(function()
    destroyed = true
    for _, connection in ipairs(connections) do pcall(function() connection:Disconnect() end) end
    Screen:Destroy()
end)

table.insert(connections, UserInputService.InputBegan:Connect(function(input, processed)
    if processed then return end
    if input.KeyCode == menuToggleKey then SetOpen(not windowOpen) end
end))

-- Responsive scale. Keeps the 1080×640 design but leaves more edge safety on smaller viewports.
local baseScale = 1
GetTargetScale = function()
    return math.clamp(baseScale * uiScaleFactor, 0.5, 1.15)
end
local function UpdateResponsiveScale()
    local camera = workspace.CurrentCamera
    if not camera then return end
    local viewport = camera.ViewportSize
    local fitX = (viewport.X - 72) / 900
    local fitY = (viewport.Y - 72) / 540
    baseScale = math.clamp(math.min(fitX, fitY, 1), 0.55, 1)
    if windowOpen then Scale.Scale = GetTargetScale() end
end
if workspace.CurrentCamera then
    table.insert(connections, workspace.CurrentCamera:GetPropertyChangedSignal("ViewportSize"):Connect(UpdateResponsiveScale))
end
UpdateResponsiveScale()

-- Initial state and entrance.
if pages.Home and pages.Home.Group then
    pages.Home.Group.Visible = true
    pages.Home.Group.GroupTransparency = 0
end
ApplyContentLayout(false, true)
pcall(function() SelectPage("Home") end)
-- Ensure window is interactive even if tweens fail
Window.Visible = true
Window.Active = true
Window.GroupTransparency = 0
Scale.Scale = GetTargetScale()
-- Soft entrance (non-blocking)
pcall(function()
    Window.GroupTransparency = 1
    Scale.Scale = GetTargetScale() * 0.94
    Tween(Window, 0.4, {GroupTransparency = 0})
    Tween(Scale, 0.45, {Scale = GetTargetScale()})
    task.delay(0.5, function()
        if Window and Window.Parent then
            Window.GroupTransparency = 0
            Window.Visible = true
            Window.Active = true
        end
    end)
end)
task.delay(0.5, function()
    if Screen.Parent then Toast("Blade Ball Lyra", "Lyra redesign loaded and Blade Ball features ready.") end
end)
task.spawn(function()
    task.wait(0.75)
    local autoName = ConfigManager.GetAutoload()
    if autoName then
        local ok, res = ConfigManager.LoadAutoload()
        if Screen.Parent then
            notify("Autoload", ok and ("Loaded '" .. autoName .. "' (" .. tostring(res) .. " flags)") or ("Failed: " .. tostring(res)), 4)
        end
    end
end)

-- Hard safety: keep the shell usable no matter what
task.defer(function()
    pcall(function()
        if Window and Window.Parent then
            Window.Visible = true
            Window.Active = true
            Window.GroupTransparency = 0
            if Scale then
                local s = 1
                pcall(function() s = GetTargetScale() end)
                if type(s) ~= "number" or s ~= s or s <= 0 then s = 0.84 end
                Scale.Scale = s
            end
        end
        if pages and pages.Home and pages.Home.Group then
            pages.Home.Group.Visible = true
            pages.Home.Group.GroupTransparency = 0
        end
    end)
end)
end
FinalizeUI()

end
